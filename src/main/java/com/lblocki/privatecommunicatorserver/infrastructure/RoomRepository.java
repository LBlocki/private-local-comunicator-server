package com.lblocki.privatecommunicatorserver.infrastructure;

import com.lblocki.privatecommunicatorserver.domain.Room;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface RoomRepository extends CrudRepository<Room, Long> {
    Optional<Room> findById(final Long id);

    Set<Room> findAllByUsers_username(final String username);
}
