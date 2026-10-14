package com.farmaplux.farmaplux.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmaplux.farmaplux.model.Categoria;

public interface CategoriaService {

    Categoria guardar(Categoria categoria);

    List<Categoria> listar();

    Page<Categoria> listar(Pageable pageable);

    Optional<Categoria> buscarPorId(String id);

    Page<Categoria> buscarPorNombre(String nombre, Pageable pageable);

    void eliminar(String id);
}
