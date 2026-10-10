package com.taskflow.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("0123456789abcdef0123456789abcdef".getBytes());

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 60_000);
    }

    @Test
    void generatedTokenIsValidAndContainsEmail() {
        String token = jwtService.generateToken("ahmed@test.com");

        assertTrue(jwtService.isValid(token));
        assertEquals("ahmed@test.com", jwtService.extractEmail(token));
    }

    @Test
    void tamperedTokenIsInvalid() {
        String token = jwtService.generateToken("ahmed@test.com");

        assertFalse(jwtService.isValid(token + "x"));
        assertFalse(jwtService.isValid("abc.def.ghi"));
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtService shortLived = new JwtService(SECRET, -1000);
        String token = shortLived.generateToken("ahmed@test.com");

        assertFalse(shortLived.isValid(token));
    }
}