package com.farmaplux.farmaplux.service;

import com.farmaplux.farmaplux.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovimientoService {

    /**
     * Registra un movimiento a nombre del usuario autenticado.
     * Nunca lanza excepciones: un fallo al registrar no debe romper la operación original.
     */
    void registrar(String modulo, String accion, String entidadId, String descripcion);

    Page<Movimiento> buscar(String q, String modulo, Pageable pageable);
}
