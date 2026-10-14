package com.farmaplux.farmaplux.repository;

import com.farmaplux.farmaplux.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MovimientoRepository extends MongoRepository<Movimiento, String> {

    Page<Movimiento> findByModulo(String modulo, Pageable pageable);

    @Query("{ '$or': [ "
            + "{ 'descripcion':   { '$regex': ?0, '$options': 'i' } }, "
            + "{ 'usuarioNombre': { '$regex': ?0, '$options': 'i' } }, "
            + "{ 'usuarioCorreo': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Movimiento> buscar(String regex, Pageable pageable);

    @Query("{ 'modulo': ?1, '$or': [ "
            + "{ 'descripcion':   { '$regex': ?0, '$options': 'i' } }, "
            + "{ 'usuarioNombre': { '$regex': ?0, '$options': 'i' } }, "
            + "{ 'usuarioCorreo': { '$regex': ?0, '$options': 'i' } } ] }")
    Page<Movimiento> buscarPorModulo(String regex, String modulo, Pageable pageable);
}
