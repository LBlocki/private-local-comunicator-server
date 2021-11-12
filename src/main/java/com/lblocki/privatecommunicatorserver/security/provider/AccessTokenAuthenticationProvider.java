package com.lblocki.privatecommunicatorserver.security.provider;

import com.lblocki.privatecommunicatorserver.security.authentication.AccessAuthenticationToken;
import com.lblocki.privatecommunicatorserver.usecase.JWTTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

@RequiredArgsConstructor
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final JWTTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        final String authenticationToken = authentication.getName().split(" ")[1];
        final Claims extractedClaims = jwtTokenService.extractClaims(authenticationToken);

        val authenticatedUser = userDetailsService.loadUserByUsername(extractedClaims.getSubject());
        ((User) authenticatedUser).eraseCredentials();
        return new AccessAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return AccessAuthenticationToken.class.equals(aClass);
    }
}
