package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.farmaplux.farmaplux.model.Categoria;

public interface CategoriaRepository extends MongoRepository<Categoria, String> {

    boolean existsByNombreIgnoreCase(String nombre);

    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
