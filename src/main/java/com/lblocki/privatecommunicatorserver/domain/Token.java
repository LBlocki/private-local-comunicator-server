package com.lblocki.privatecommunicatorserver.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Column(name = "code", nullable = false, length = 1024)
    private String code;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType type;

    @Column(name = "create_date", nullable = false)
    private Timestamp createDate;

    public enum TokenType {
        ACCESS_TOKEN, REFRESH_TOKEN
    }
}
