package com.lblocki.privatecommunicatorserver.usecase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.FixedClock;
import lombok.val;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class JWTTokenService {

    @Value("${authentication.token.validation.time.milliseconds}")
    private Long authenticationTokenValidationTimeInMilliseconds;

    @Value("${authentication.token.secret}")
    private String authenticationTokenSecret;

    public Claims extractClaims(final String tokenCode) {

        Validate.notBlank(tokenCode);

        return Jwts.parser()
                .setClock(new FixedClock(Date.from(LocalDateTime.now().atZone(
                        ZoneId.of(ZoneOffset.UTC.getId())).toInstant()))
                )
                .setSigningKey(authenticationTokenSecret)
                .parseClaimsJws(tokenCode)
                .getBody();
    }

    public String generateTokenCode(final String username) {

        Validate.notBlank(username);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer("Private communicator")
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of(ZoneOffset.UTC.getId())).toInstant()))
                .setExpiration(calculateExpirationTime())
                .setHeaderParam("user_type", "USER")
                .signWith(SignatureAlgorithm.HS512, authenticationTokenSecret).compact();
    }

    private Date calculateExpirationTime() {
        val now = LocalDateTime.now().atZone(ZoneId.of(ZoneOffset.UTC.getId())).toInstant().toEpochMilli();
        return Date.from(Instant.ofEpochMilli(now + authenticationTokenValidationTimeInMilliseconds)
                .atZone(ZoneId.of(ZoneOffset.UTC.getId())).toInstant());
    }
}
