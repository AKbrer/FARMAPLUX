package com.farmaplux.farmaplux.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.farmaplux.farmaplux.model.Usuario;
import com.farmaplux.farmaplux.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService
                implements UserDetailsService {

        private final UsuarioRepository repository;

        public CustomUserDetailsService(
                        UsuarioRepository repository) {

                this.repository = repository;
        }

        @Override
        public UserDetails loadUserByUsername(String email)
                        throws UsernameNotFoundException {

                Usuario usuario = repository
                                .findByCorreo(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado"));

                return User.builder()
                                .username(usuario.getCorreo())
                                .password(usuario.getPassword())
                                .roles(usuario.getRol()
                                                .replace("ROLE_", ""))
                                .build();
        }

}
