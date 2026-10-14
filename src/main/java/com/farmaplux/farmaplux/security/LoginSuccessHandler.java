package com.farmaplux.farmaplux.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles = AuthorityUtils
                .authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_ADMIN")) {

            response.sendRedirect("/admin");

        } else if (roles.contains("ROLE_CLIENTE")) {

            response.sendRedirect("/catalogo");

        } else {

            response.sendRedirect("/");
        }
    }
}