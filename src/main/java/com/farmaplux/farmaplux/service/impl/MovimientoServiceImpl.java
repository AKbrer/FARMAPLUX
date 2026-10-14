package com.farmaplux.farmaplux.service.impl;

import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.repository.MovimientoRepository;
import com.farmaplux.farmaplux.service.MovimientoService;
import com.farmaplux.farmaplux.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MovimientoServiceImpl implements MovimientoService {

    /** Zona horaria usada para la fecha/hora de cada movimiento. */
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final MovimientoRepository movimientoRepository;
    private final UsuarioService usuarioService;

    public MovimientoServiceImpl(MovimientoRepository movimientoRepository, UsuarioService usuarioService) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void registrar(String modulo, String accion, String entidadId, String descripcion) {
        try {
            String correo = "desconocido";
            String nombre = "Desconocido";

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
                correo = auth.getName();
                nombre = usuarioService.buscarPorCorreo(correo)
                        .map(u -> u.getNombre())
                        .orElse(correo);
            }

            movimientoRepository.save(Movimiento.builder()
                    .fecha(LocalDateTime.now(ZONA))
                    .usuarioNombre(nombre)
                    .usuarioCorreo(correo)
                    .modulo(modulo)
                    .accion(accion)
                    .entidadId(entidadId)
                    .descripcion(descripcion)
                    .build());
        } catch (Exception e) {
            log.error("No se pudo registrar el movimiento [{} {}]: {}", modulo, accion, e.getMessage());
        }
    }

    @Override
    public Page<Movimiento> buscar(String q, String modulo, Pageable pageable) {
        boolean hayTexto = q != null && !q.isBlank();
        boolean hayModulo = modulo != null && !modulo.isBlank();

        if (hayTexto) {
            String regex = Pattern.quote(q.trim());
            return hayModulo
                    ? movimientoRepository.buscarPorModulo(regex, modulo, pageable)
                    : movimientoRepository.buscar(regex, pageable);
        }
        return hayModulo
                ? movimientoRepository.findByModulo(modulo, pageable)
                : movimientoRepository.findAll(pageable);
    }
}
