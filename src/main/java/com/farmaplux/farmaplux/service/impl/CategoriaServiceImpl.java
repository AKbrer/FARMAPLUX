package com.farmaplux.farmaplux.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.farmaplux.farmaplux.model.Categoria;
import com.farmaplux.farmaplux.repository.CategoriaRepository;
import com.farmaplux.farmaplux.service.CategoriaService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        if (categoria.getId() == null
                && categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        }
        return categoriaRepository.save(categoria);
    }

    @Override
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    public Page<Categoria> listar(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    @Override
    public Optional<Categoria> buscarPorId(String id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Page<Categoria> buscarPorNombre(String nombre, Pageable pageable) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public void eliminar(String id) {
        categoriaRepository.deleteById(id);
    }
}
