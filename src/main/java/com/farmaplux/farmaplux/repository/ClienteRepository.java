package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.farmaplux.farmaplux.model.Cliente;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

    boolean existsByDocumento(String documento);

    Page<Cliente> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Cliente> findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(
            String nombre, String documento, Pageable pageable);
}
