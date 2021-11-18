package com.lblocki.privatecommunicatorserver.infrastructure;

import com.lblocki.privatecommunicatorserver.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(final String username);

    Set<User> findAll();

    Set<User> findAllByUsernameIn(final Set<String> username);
}
