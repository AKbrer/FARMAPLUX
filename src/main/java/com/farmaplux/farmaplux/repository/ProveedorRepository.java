package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.farmaplux.farmaplux.model.Proveedor;

public interface ProveedorRepository extends MongoRepository<Proveedor, String> {

    boolean existsByNombreIgnoreCase(String nombre);

    Page<Proveedor> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
