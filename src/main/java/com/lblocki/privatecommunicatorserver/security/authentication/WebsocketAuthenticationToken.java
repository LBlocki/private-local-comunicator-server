package com.lblocki.privatecommunicatorserver.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class WebsocketAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public WebsocketAuthenticationToken(final Object principal, final Object credentials) {
        super(principal, credentials);
    }

    public WebsocketAuthenticationToken(final Object principal,
                                        final Object credentials,
                                        final Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
