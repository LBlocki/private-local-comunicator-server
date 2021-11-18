package com.lblocki.privatecommunicatorserver.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "read_by_recipient")
    @Enumerated(EnumType.STRING)
    private ReadByRecipient readByRecipient;

    @Column(name = "body", nullable = false, length = 128)
    private String body;

    @Column(name = "creation_date", nullable = false)
    private Timestamp creationDate;

    public enum ReadByRecipient {
        Y
    }
}
