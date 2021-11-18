package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;

@Value
@Builder
@Jacksonized
public class RoomMessagesDTO {
    RoomDTO room;
    Collection<MessageDTO> messages;
}
