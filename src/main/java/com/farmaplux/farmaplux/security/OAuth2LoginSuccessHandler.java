package com.farmaplux.farmaplux.security;

import com.farmaplux.farmaplux.model.Usuario;
import com.farmaplux.farmaplux.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final long jwtExpiration;

    public OAuth2LoginSuccessHandler(UsuarioService usuarioService, JwtUtil jwtUtil,
                                     @Value("${jwt.expiration}") long jwtExpiration) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");

        Optional<Usuario> existing = usuarioService.buscarPorCorreo(email);

        Usuario usuario;
        if (existing.isPresent()) {
            usuario = existing.get();
        } else {
            usuario = Usuario.builder()
                    .correo(email)
                    .nombre(nombre)
                    .password("") // no password for OAuth2 users
                    .rol("ROLE_CLIENTE")
                    .build();
            usuarioService.guardar(usuario);
        }

        String token = jwtUtil.generateToken(email, usuario.getRol().replace("ROLE_", ""));

        String cookieHeader = String.format("jwt=%s; HttpOnly; Path=/; Max-Age=%d; SameSite=Lax",
                token, jwtExpiration / 1000);
        response.addHeader("Set-Cookie", cookieHeader);

        if ("ROLE_ADMIN".equals(usuario.getRol())) {
            response.sendRedirect("/admin");
        } else {
            response.sendRedirect("/catalogo");
        }
    }
}
