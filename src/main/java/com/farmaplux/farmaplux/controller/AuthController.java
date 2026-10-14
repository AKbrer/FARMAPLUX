package com.farmaplux.farmaplux.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.farmaplux.farmaplux.model.Usuario;
import com.farmaplux.farmaplux.security.JwtUtil;
import com.farmaplux.farmaplux.service.UsuarioService;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario) {
        usuario.setRol("ROLE_CLIENTE");
        usuarioService.guardar(usuario);
        return "redirect:/login";
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> body,
                                   HttpServletResponse response) {
        String correo = body.get("correo");
        String password = body.get("password");

        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, password));

            var usuario = usuarioService.buscarPorCorreo(correo).orElseThrow();

            String token = jwtUtil.generateToken(correo, usuario.getRol().replace("ROLE_", ""));

            String cookieHeader = String.format("jwt=%s; HttpOnly; Path=/; Max-Age=%d; SameSite=Lax",
                    token, jwtExpiration / 1000);
            response.addHeader("Set-Cookie", cookieHeader);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "nombre", usuario.getNombre(),
                    "correo", usuario.getCorreo(),
                    "rol", usuario.getRol()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
