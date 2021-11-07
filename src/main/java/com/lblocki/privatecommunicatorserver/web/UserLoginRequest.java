package com.lblocki.privatecommunicatorserver.web;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserLoginRequest {
    String username;
    String password;
}
