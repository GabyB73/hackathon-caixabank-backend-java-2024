package com.hackathon.bankingapp.utils;

import com.hackathon.bankingapp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.UUID;

@Component
public class TokenUtil {

    //@Value("${jwt.secret}")// Clave secreta para firmar el JWT, definida en application.properties
    private String secretKey = "304f42d8711889585a433a9ec28ebae231a3c379c61b6199023b6ae00a6d4a3d";

    @Value("${jwt.expiration}") // Tiempo de expiración en milisegundos, también en application.properties
    private long expirationTime;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(user.getAccountNumber().toString()) // Usar el número de cuenta como sujeto
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        return extractUsername(token) != null && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public UUID extractAccountNumberFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return UUID.fromString(claims.getSubject()); // Convertir el sujeto de vuelta a UUID
        } catch (Exception e) {
            // Manejar la excepción, tal vez lanzar una excepción personalizada
            throw new RuntimeException("Token no válido", e);
        }
    }
}
