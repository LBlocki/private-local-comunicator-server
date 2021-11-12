package com.lblocki.privatecommunicatorserver.web.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserRegisterRequest {
    String username;
    String password;
}
