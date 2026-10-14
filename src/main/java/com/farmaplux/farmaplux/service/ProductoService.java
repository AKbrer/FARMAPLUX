package com.farmaplux.farmaplux.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmaplux.farmaplux.model.Producto;

public interface ProductoService {

    Producto guardar(Producto producto);

    /** Valida los datos del producto; lanza RuntimeException con un mensaje legible si algo es inválido. */
    void validar(Producto producto);

    List<Producto> listar();

    Page<Producto> listar(Pageable pageable);

    Optional<Producto> buscarPorId(String id);

    Optional<Producto> buscarPorCodigoBarras(String codigo);

    List<Producto> buscarStockBajo(int stock);

    List<Producto> buscarPorNombre(String nombre);

    Page<Producto> buscarPorNombre(String nombre, Pageable pageable);

    Page<Producto> buscarPorCategoria(String categoriaId, Pageable pageable);

    Page<Producto> buscarPorNombreYCategoria(String nombre, String categoriaId, Pageable pageable);

    void eliminar(String id);
}
