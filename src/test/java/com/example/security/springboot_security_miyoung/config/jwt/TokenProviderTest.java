package com.example.security.springboot_security_miyoung.config.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {
    @Test
    void generate_secret_key() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String encoded = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println(encoded);
    }


}