package com.hackathon.bankingapp.utils;

import com.hackathon.bankingapp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.annotations.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.UUID;

@Component
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    @Value("${jwt.secret}") // Clave secreta para firmar el JWT, definida en application.properties
    private String secretKey;

    @Value("${jwt.expiration}") // Tiempo de expiración en milisegundos, también en application.properties
    private long expirationTime;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        UUID accountNumberUUID = UUIDUtil.fromBytes(user.getAccountNumber());

        String token = Jwts.builder()
                .setSubject(accountNumberUUID.toString()) // Usar el número de cuenta como sujeto
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        logger.info("Generated token for user: {}", accountNumberUUID);
        logger.debug("Generated token: {}", token);
        return token;
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            logger.debug("Validating token: {}", token);
            return extractAccountNumberFromToken(token) != null && !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            logger.debug("Checking if token is expired: {}", token);
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            logger.error("Token expiration check error: {}", e.getMessage());
            return true;

        }
    }

    public UUID extractAccountNumberFromToken(String token) {
        try {
            logger.debug("Extracting account number from token: {}", token);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            UUID accountNumber = UUID.fromString(claims.getSubject());
            logger.info("Extracted account number: {}", accountNumber);
            return accountNumber;
        } catch (Exception e) {
            logger.error("Error extracting account number from token: {}", e.getMessage());
            throw new RuntimeException("Token no válido", e);
        }
    }
}
