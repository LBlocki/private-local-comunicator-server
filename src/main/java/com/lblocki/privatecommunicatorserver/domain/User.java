package com.lblocki.privatecommunicatorserver.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 128)
    private String username;

    @Column(name = "wrapped_private_key", nullable = false, unique = true, length = 2048)
    String wrappedPrivateKey;

    @Column(name = "exported_public_key", nullable = false, unique = true, length = 512)
    String exportedPublicKey;

    @Column(name = "wrapped_symmetric_key", nullable = false, unique = true, length = 512)
    String wrappedSymmetricKey;

    @Column(name = "iv_for_private_key", nullable = false, unique = true)
    String ivForPrivateKey;

    @Column(name = "iv_for_symmetric_key", nullable = false, unique = true)
    String ivForSymmetricKey;
}
