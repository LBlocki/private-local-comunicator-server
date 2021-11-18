package com.lblocki.privatecommunicatorserver.web.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserDTO {
    Long id;
    String username;
}
