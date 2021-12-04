package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Collection;

@Value
@Builder
@Jacksonized
public class InitialDataDTO {
    Collection<RoomMessagesDTO> roomMessagesList;
    String wrappedPrivateKey;
    String exportedPublicKey;
    String ivForPrivateKey;
}
