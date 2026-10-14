package com.farmaplux.farmaplux.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", "jwt=; HttpOnly; Path=/; Max-Age=0; SameSite=Lax");
        return "logout";
    }
}
