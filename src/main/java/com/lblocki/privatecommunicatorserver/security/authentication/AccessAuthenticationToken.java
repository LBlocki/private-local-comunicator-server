package com.lblocki.privatecommunicatorserver.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccessAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public AccessAuthenticationToken(final Object principal,
                                     final Object credentials) {
        super(principal, credentials);
    }

    public AccessAuthenticationToken(final Object principal,
                                     final Object credentials,
                                     final Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}