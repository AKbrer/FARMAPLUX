package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.farmaplux.farmaplux.model.Venta;

import java.time.LocalDate;
import java.util.List;

public interface VentaRepository extends MongoRepository<Venta, String> {

    List<Venta> findByFecha(LocalDate fecha);

    List<Venta> findByUsuarioId(String usuarioId);

    Page<Venta> findByMetodoPagoContainingIgnoreCase(String metodoPago, Pageable pageable);
}