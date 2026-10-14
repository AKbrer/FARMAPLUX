package com.farmaplux.farmaplux.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.farmaplux.farmaplux.model.Categoria;
import com.farmaplux.farmaplux.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    List<Producto> findByStockLessThan(int stock);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Page<Producto> findByCategoria(Categoria categoria, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseAndCategoria(String nombre, Categoria categoria, Pageable pageable);

    boolean existsByCodigoBarras(String codigoBarras);
}
