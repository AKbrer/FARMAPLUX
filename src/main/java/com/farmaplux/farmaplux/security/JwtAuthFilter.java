package com.farmaplux.farmaplux.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String token = extractToken(request);

        if (token != null) {
            log.info("JwtAuthFilter: token encontrado para {} (longitud={})", path, token.length());
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                String rol = jwtUtil.getRolFromToken(token);
                log.info("JwtAuthFilter: token VALIDO para {} con rol={}", email, rol);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

                var auth = new UsernamePasswordAuthenticationToken(
                        email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.warn("JwtAuthFilter: token INVALIDO para {}", path);
            }
        } else {
            log.info("JwtAuthFilter: NO hay token para {}", path);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                log.info("JwtAuthFilter: cookie encontrada: {}={}", c.getName(), c.getValue().substring(0, Math.min(20, c.getValue().length())) + "...");
                if ("jwt".equals(c.getName())) {
                    return c.getValue();
                }
            }
        } else {
            log.info("JwtAuthFilter: NO hay cookies en la request");
        }

        return null;
    }
}
