package com.lblocki.privatecommunicatorserver.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

@Data
@Entity
@Table(name = "Messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Collection<MessageBody> messageBodies = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "read_by_recipient")
    @Enumerated(EnumType.STRING)
    private ReadByRecipient readByRecipient;

    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    public enum ReadByRecipient {
        Y
    }
}
