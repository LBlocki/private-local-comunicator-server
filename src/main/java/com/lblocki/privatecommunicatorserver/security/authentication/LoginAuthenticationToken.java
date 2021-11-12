package com.lblocki.privatecommunicatorserver.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class LoginAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public LoginAuthenticationToken(final Object principal, final Object credentials) {
        super(principal, credentials);
    }

    public LoginAuthenticationToken(final Object principal,
                                    final Object credentials,
                                    final Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
