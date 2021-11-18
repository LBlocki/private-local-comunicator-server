package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.sql.Timestamp;

@Value
@Builder
@Jacksonized
public class MessageDTO {
    Long id;
    String creatorUsername;
    Long roomId;
    String body;
    Boolean readByRecipient;
    Timestamp creationDate;
}
