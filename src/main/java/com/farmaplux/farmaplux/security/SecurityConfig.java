package com.farmaplux.farmaplux.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final CustomUserDetailsService userDetailsService;
        private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                        CustomUserDetailsService userDetailsService,
                        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
                this.jwtAuthFilter = jwtAuthFilter;
                this.userDetailsService = userDetailsService;
                this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                 .authorizeHttpRequests(auth -> auth
                                 .requestMatchers(
                                                                  "/",
                                                                  "/css/**",
                                                                  "/js/**",
                                                                  "/api/auth/login",
                                                                 "/login",
                                                                 "/logout",
                                                                 "/registro",
                                                                 "/login/oauth2/**",
                                                                 "/oauth2/**",
                                                                 "/usuarios/acceso-denegado")
                                                 .permitAll()

                                                 .requestMatchers("/admin/**",
                                                                 "/usuarios/**", "/ventas/**", "/categorias/**",
                                                                 "/productos/**", "/proveedores/**", "/clientes/**",
                                                                 "/movimientos/**")
                                                 .hasRole("ADMIN")

                                                 .requestMatchers("/catalogo/**")
                                                 .hasAnyRole("ADMIN", "CLIENTE")

                                                 .anyRequest()
                                                 .authenticated())

                                 .oauth2Login(oauth2 -> oauth2
                                                 .loginPage("/login")
                                                 .successHandler(oAuth2LoginSuccessHandler))

                                 .addFilterBefore(jwtAuthFilter, AnonymousAuthenticationFilter.class)

                                 .exceptionHandling(ex -> ex
                                                 .authenticationEntryPoint(this::commence)
                                                 .accessDeniedPage("/usuarios/acceso-denegado"));

                return http.build();
        }

        private void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
                String accept = request.getHeader("Accept");
                if (accept != null && accept.contains("application/json")) {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"No autorizado\"}");
                } else {
                        response.sendRedirect("/login");
                }
        }

    @Bean
    AuthenticationProvider authenticationProvider(BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
