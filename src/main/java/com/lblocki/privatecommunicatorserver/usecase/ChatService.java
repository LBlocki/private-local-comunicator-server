package com.lblocki.privatecommunicatorserver.usecase;

import com.lblocki.privatecommunicatorserver.domain.Message;
import com.lblocki.privatecommunicatorserver.domain.MessageBody;
import com.lblocki.privatecommunicatorserver.domain.Room;
import com.lblocki.privatecommunicatorserver.domain.User;
import com.lblocki.privatecommunicatorserver.infrastructure.MessageRepository;
import com.lblocki.privatecommunicatorserver.infrastructure.RoomRepository;
import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.web.dto.MessageDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public void setMessagesAsRead(final Long roomId) throws IllegalAccessException {

        if (Objects.isNull(roomId)) {
            throw new IllegalArgumentException("Room id must be provided");
        }

        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        validateExistenceAndAccessOfRoom(roomId, username);

        final Set<Message> messages = messageRepository.findAllByRecipient_UsernameAndReadByRecipientIsNullAndRoom_IdEquals(username, roomId)
                .stream()
                .peek(message -> message.setReadByRecipient(Message.ReadByRecipient.Y))
                .collect(Collectors.toSet());

        messageRepository.saveAll(messages);
    }

    public User fetchCurrentUser() {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Failed to find user " + username));
    }

    public Collection<Room> fetchAllRooms() {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return roomRepository.findAllByUsers_username(username);
    }

    public Collection<Message> fetchAllMessages() {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return messageRepository.findAllByCreator_UsernameOrRecipient_Username(username, username);
    }

    @Transactional
    public Message persistMessage(final MessageDTO messageDTO,
                                  final String recipientUsername,
                                  final String creatorUsername) throws IllegalAccessException {

        this.validateMessagePersistenceRequest(messageDTO, recipientUsername);

        final Set<User> users = userRepository.findAllByUsernameIn(Set.of(creatorUsername, recipientUsername));

        final User creator = users.stream()
                .filter(user -> user.getUsername().equals(creatorUsername))
                .findFirst()
                .orElseThrow();
        final User recipient = users.stream()
                .filter(user -> user.getUsername().equals(recipientUsername))
                .findFirst()
                .orElseThrow();

        final Room room = validateAccessIfRoomExist(messageDTO.getRoomId(), creator, recipient);

        final Message messageToPersist = new Message();
        messageToPersist.setCreationDate(Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)));
        messageToPersist.setRecipient(recipient);
        messageToPersist.setReadByRecipient(null);
        messageToPersist.setRoom(room);
        messageToPersist.setCreator(creator);

        final Message createdMessage = messageRepository.save(messageToPersist);

        final Collection<MessageBody> messageBodies = messageDTO.getMessageBodies().stream().map(mb -> {
            final MessageBody messageBody = new MessageBody();
            messageBody.setRecipient(mb.getRecipient());
            messageBody.setBody(mb.getBody());
            return messageBody;
        }).collect(Collectors.toSet());


        createdMessage.setMessageBodies(messageBodies);

        return messageRepository.save(createdMessage);
    }

    private Room validateAccessIfRoomExist(@NonNull final Long roomId,
                                           @NonNull final User creator,
                                           @NonNull final User recipient) throws IllegalAccessException {

        final Optional<Room> existingRoom = roomRepository.findById(roomId);

        if (existingRoom.isEmpty()) {
            throw new IllegalArgumentException("Provided room does not exist");
        }

        final boolean hasAccess = existingRoom.get().getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet())
                .containsAll(Set.of(creator.getUsername(), recipient.getUsername()));

        if (!hasAccess) {
            throw new IllegalAccessException("One or more users specified in message" +
                    " does not have access to specified room");
        }

        return existingRoom.get();
    }

    private void validateExistenceAndAccessOfRoom(@NonNull final Long roomId,
                                                  @NonNull final String recipient) throws IllegalAccessException {

        final Optional<Room> existingRoom = roomRepository.findById(roomId);

        if (existingRoom.isEmpty()) {
            throw new IllegalArgumentException("Provided room does not exist");
        }

        final boolean hasAccess = existingRoom.get().getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet())
                .contains(recipient);

        if (!hasAccess) {
            throw new IllegalAccessException("One or more users specified in message" +
                    " does not have access to specified room");
        }
    }

    private void validateMessagePersistenceRequest(final MessageDTO messageDTO, final String recipientUsername) {
        try {
            Validate.notBlank(recipientUsername);
            Validate.notNull(messageDTO);
            Validate.notEmpty(messageDTO.getMessageBodies());
            messageDTO.getMessageBodies().forEach(body -> {
                Validate.notBlank(body.getRecipient());
                Validate.notBlank(body.getBody());
            });
            Validate.notBlank(messageDTO.getCreatorUsername());
            Validate.notNull(messageDTO.getRoomId());
            Validate.isTrue(Objects.isNull(messageDTO.getReadByRecipient()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Message persistence validation failed", ex);
        }
    }
}
