package com.lblocki.privatecommunicatorserver.infrastructure;

import com.lblocki.privatecommunicatorserver.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(final String username);
}
