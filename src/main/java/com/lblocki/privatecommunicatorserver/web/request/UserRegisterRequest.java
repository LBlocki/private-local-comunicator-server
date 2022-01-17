package com.lblocki.privatecommunicatorserver.web.request;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserRegisterRequest {
    String username;
    String wrappedPrivateKey;
    String exportedPublicKey;
    String wrappedSymmetricKey;
    String ivForPrivateKey;
    String ivForSymmetricKey;
}
