package com.lblocki.privatecommunicatorserver.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Message_bodies")
public class MessageBody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient", nullable = false, length = 256)
    private String recipient;

    @Column(name = "message", nullable = false, length = 2048)
    private String body;
}
