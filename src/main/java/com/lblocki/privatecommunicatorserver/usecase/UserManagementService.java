package com.lblocki.privatecommunicatorserver.usecase;

import com.lblocki.privatecommunicatorserver.domain.User;
import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.utils.UserAlreadyExistException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(@NonNull final String username, @NonNull final String password) {

        validateUserNotExistingOrThrow(username);

        final User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));

        userRepository.save(newUser);
    }


    private void validateUserNotExistingOrThrow(@NonNull final String username) {
        final Optional<User> user = userRepository.findByUsername(username);

        if(user.isPresent()) {
            throw new UserAlreadyExistException("User " + username + " already exists");
        }
    }
}
