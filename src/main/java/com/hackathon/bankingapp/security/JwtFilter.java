package com.hackathon.bankingapp.security;

import com.hackathon.bankingapp.utils.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        // Comprueba que el encabezado contiene un token válido
        try {
            username = tokenUtil.extractUsername(token);
            logger.info("Token válido para el usuario: {}", username);
        } catch (Exception e) {
            logger.error("Error al extraer el username del token: {}", e.getMessage());
        }
    }

        // Verifica si el token es válido y si el contexto de seguridad está vacío
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (tokenUtil.validateToken(token)) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("Autenticación establecida para el usuario: {}", username);
        } else {
            logger.warn("Token inválido para el usuario: {}", username);
        }
    }

        // Continúa con el filtro
        filterChain.doFilter(request, response);
    }

}
