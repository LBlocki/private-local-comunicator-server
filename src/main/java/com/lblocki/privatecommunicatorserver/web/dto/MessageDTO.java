package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.sql.Timestamp;
import java.util.Collection;

@Value
@Builder
@Jacksonized
public class MessageDTO {
    Long id;
    String creatorUsername;
    Long roomId;
    Collection<MessageBodyDTO> messageBodies;
    Boolean readByRecipient;
    Timestamp creationDate;

    @Value
    @Jacksonized
    @Builder
    public static class MessageBodyDTO {
        String recipient;
        String body;
    }
}
