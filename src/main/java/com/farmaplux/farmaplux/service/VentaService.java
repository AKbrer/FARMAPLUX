package com.farmaplux.farmaplux.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmaplux.farmaplux.model.Venta;

public interface VentaService {

    Venta registrar(Venta venta);

    List<Venta> listar();

    Page<Venta> listar(Pageable pageable);

    Optional<Venta> buscarPorId(String id);

    Page<Venta> buscarPorMetodoPago(String metodoPago, Pageable pageable);

    void eliminar(String id);
}
