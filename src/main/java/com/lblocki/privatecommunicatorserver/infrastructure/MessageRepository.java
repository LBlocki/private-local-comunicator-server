package com.lblocki.privatecommunicatorserver.infrastructure;

import com.lblocki.privatecommunicatorserver.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface MessageRepository extends CrudRepository<Message, Long> {

    Set<Message> findAllByCreator_UsernameOrRecipient_Username(String creator_username, String recipient_username);

    Set<Message> findAllByRecipient_UsernameAndReadByRecipientIsNullAndRoom_IdEquals(final String username, Long roomId);
}
