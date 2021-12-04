package com.lblocki.privatecommunicatorserver.web;

import com.lblocki.privatecommunicatorserver.domain.Message;
import com.lblocki.privatecommunicatorserver.domain.Room;
import com.lblocki.privatecommunicatorserver.domain.User;
import com.lblocki.privatecommunicatorserver.usecase.ChatService;
import com.lblocki.privatecommunicatorserver.web.dto.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.private.{recipient}")
    public void filterPrivateMessage(@Payload MessageDTO message,
                                     @DestinationVariable("recipient") String recipient) throws IllegalAccessException {

        final MessageDTO createdMessage =
                ChatWebSocketController.messageDTO(chatService.persistMessage(message, recipient));

        simpMessagingTemplate.convertAndSendToUser(recipient, "/exchange/chat.message", createdMessage);
        simpMessagingTemplate.convertAndSendToUser(message.getCreatorUsername(), "/exchange/chat.message", createdMessage);
    }

    @MessageMapping("/chat.private.set.messages.read.{roomId}")
    public void setRoomMessagesAsRead(@DestinationVariable("roomId") Long roomId) throws IllegalAccessException {
        chatService.setMessagesAsRead(roomId);
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        simpMessagingTemplate.convertAndSendToUser(username, "/exchange/chat.private.messages.read", roomId);
    }

    @SubscribeMapping("/chat.private.fetch.initial.data")
    public InitialDataDTO fetchRoomMessagesList() {
        final Collection<Room> rooms = chatService.fetchAllRooms();
        final Collection<RoomMessagesDTO> roomMessagesList = chatService.fetchAllMessages().stream()
                .collect(Collectors.groupingBy(Message::getRoom))
                .entrySet()
                .stream()
                .map(entry -> RoomMessagesDTO.builder()
                        .room(toRoomDTO(entry.getKey()))
                        .messages(entry.getValue().stream()
                                .map(ChatWebSocketController::messageDTO)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        final List<Long> roomIdsWithMessages = roomMessagesList.stream()
                .map(RoomMessagesDTO::getRoom)
                .map(RoomDTO::getId)
                .collect(Collectors.toList());

        roomMessagesList.addAll(rooms.stream()
                .filter(room -> !roomIdsWithMessages.contains(room.getId()))
                .map(room -> RoomMessagesDTO.builder()
                        .room(toRoomDTO(room))
                        .messages(List.of())
                        .build())
                .collect(Collectors.toList()));

        final User user = chatService.fetchCurrentUser();

        return InitialDataDTO.builder()
                .roomMessagesList(roomMessagesList)
                .exportedPublicKey(user.getExportedPublicKey())
                .ivForPrivateKey(user.getIvForPrivateKey())
                .wrappedPrivateKey(user.getWrappedPrivateKey())
                .build();
    }

    private static RoomDTO toRoomDTO(@NonNull final Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .users(room.getUsers().stream().map(ChatWebSocketController::toUserDTO).collect(Collectors.toSet()))
                .build();
    }

    private static UserDTO toUserDTO(@NonNull final User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    private static MessageDTO messageDTO(@NonNull final Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .roomId(message.getRoom().getId())
                .body(message.getBody())
                .readByRecipient(Message.ReadByRecipient.Y.equals(message.getReadByRecipient()))
                .creationDate(message.getCreationDate())
                .creatorUsername(message.getCreator().getUsername())
                .build();
    }
}
