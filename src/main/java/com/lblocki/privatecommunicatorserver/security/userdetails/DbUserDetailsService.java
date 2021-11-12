package com.lblocki.privatecommunicatorserver.security.userdetails;

import com.lblocki.privatecommunicatorserver.domain.User;
import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            Validate.notBlank(username);
        } catch (Exception t) {
            throw new UsernameNotFoundException(t.getMessage());
        }

        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to find user by username " + username));

        return convertToSecurityUser(user);
    }

    private org.springframework.security.core.userdetails.User convertToSecurityUser(@NonNull final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                List.of()
        );
    }

}
