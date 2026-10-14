package com.farmaplux.farmaplux.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmaplux.farmaplux.model.Proveedor;

public interface ProveedorService {

    Proveedor guardar(Proveedor proveedor);

    List<Proveedor> listar();

    Page<Proveedor> listar(Pageable pageable);

    Optional<Proveedor> buscarPorId(String id);

    Page<Proveedor> buscarPorNombre(String nombre, Pageable pageable);

    void eliminar(String id);
}
