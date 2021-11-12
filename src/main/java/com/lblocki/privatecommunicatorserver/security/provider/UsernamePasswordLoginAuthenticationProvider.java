package com.lblocki.privatecommunicatorserver.security.provider;

import com.lblocki.privatecommunicatorserver.security.authentication.LoginAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
public class UsernamePasswordLoginAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        val user = userDetailsService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, user.getPassword())) {
            ((User) user).eraseCredentials();
            return new LoginAuthenticationToken(username, null, List.of());
        }
        ((User) user).eraseCredentials();
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return LoginAuthenticationToken.class.equals(aClass);
    }
}
