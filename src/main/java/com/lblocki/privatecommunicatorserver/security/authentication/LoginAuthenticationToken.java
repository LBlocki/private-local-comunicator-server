package com.lblocki.privatecommunicatorserver.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class LoginAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public LoginAuthenticationToken(final Object principal, final Object credentials) {
        super(principal, credentials);
    }
}
