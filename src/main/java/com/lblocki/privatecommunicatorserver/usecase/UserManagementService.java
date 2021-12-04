package com.lblocki.privatecommunicatorserver.usecase;

import com.lblocki.privatecommunicatorserver.domain.Room;
import com.lblocki.privatecommunicatorserver.domain.User;
import com.lblocki.privatecommunicatorserver.infrastructure.RoomRepository;
import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.utils.UserAlreadyExistException;
import com.lblocki.privatecommunicatorserver.web.dto.RoomDTO;
import com.lblocki.privatecommunicatorserver.web.dto.UserDTO;
import com.lblocki.privatecommunicatorserver.web.request.UserRegisterRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void registerUser(@NonNull UserRegisterRequest request) {

        validateUserNotExistingOrThrow(request.getUsername());

        final User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setIvForPrivateKey(request.getIvForPrivateKey());
        newUser.setIvForSymmetricKey(request.getIvForSymmetricKey());
        newUser.setWrappedPrivateKey(request.getWrappedPrivateKey());
        newUser.setExportedPublicKey(request.getExportedPublicKey());
        newUser.setWrappedSymmetricKey(passwordEncoder.encode(request.getWrappedSymmetricKey()));

        final User createdUser = userRepository.save(newUser);
        final List<Room> newRoomList = new ArrayList<>();

        final Set<User> userSet = userRepository.findAll().stream()
                .filter(val -> !val.getUsername().equals(request.getUsername()))
                .peek(user -> {
                    final Room newRoom = new Room();
                    newRoom.setUsers(Set.of(user, createdUser));
                    newRoomList.add(newRoom);
                }).collect(Collectors.toSet());

        roomRepository.saveAll(newRoomList);

        userSet.forEach(user -> {
            final RoomDTO room = newRoomList.stream()
                    .filter(val -> val.getUsers().contains(user))
                    .findFirst()
                    .map(UserManagementService::toRoomDTO)
                    .orElseThrow(() -> new RuntimeException("Failed to find new room for user " + user.getUsername()));

            simpMessagingTemplate.convertAndSendToUser(user.getUsername(), "/exchange/chat.room", room);
        });
    }

    public String getUserIvForWrappedSymmetricKey(final String username) {
        validateUserExistOrThrow(username);
        return userRepository.findByUsername(username).stream().map(User::getIvForSymmetricKey).findFirst().orElseThrow();
    }

    private static RoomDTO toRoomDTO(@NonNull final Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .users(room.getUsers().stream().map(UserManagementService::toUserDTO).collect(Collectors.toSet()))
                .build();
    }

    private static UserDTO toUserDTO(@NonNull final User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }


    private void validateUserNotExistingOrThrow(@NonNull final String username) {
        final Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            throw new UserAlreadyExistException("User " + username + " already exists");
        }
    }

    private void validateUserExistOrThrow(final String username) {
        final Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()) {
            throw new IllegalArgumentException("User " + username + " does not exists");
        }
    }
}
