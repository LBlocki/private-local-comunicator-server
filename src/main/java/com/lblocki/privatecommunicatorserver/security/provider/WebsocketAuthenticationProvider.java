package com.lblocki.privatecommunicatorserver.security.provider;

import com.lblocki.privatecommunicatorserver.security.authentication.WebsocketAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class WebsocketAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        validateWebsocketAuthentication(username, password);

        val user = userDetailsService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            ((User) user).eraseCredentials();
            return new WebsocketAuthenticationToken(username, null, List.of());
        }
        ((User) user).eraseCredentials();
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return WebsocketAuthenticationToken.class.equals(aClass);
    }

    private void validateWebsocketAuthentication(final String username, final String password) {
        if(Objects.isNull(username) || username.trim().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Username is null or empty");
        }

        if(Objects.isNull(password) || password.trim().isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("Password is null or empty");
        }
    }
}
