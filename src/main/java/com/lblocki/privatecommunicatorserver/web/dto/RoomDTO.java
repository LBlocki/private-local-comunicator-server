package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;

@Value
@Builder
@Jacksonized
public class RoomDTO {
    Long id;
    Set<UserDTO> users;
}
