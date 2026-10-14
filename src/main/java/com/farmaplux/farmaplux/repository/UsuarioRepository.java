package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.farmaplux.farmaplux.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
