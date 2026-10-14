package com.farmaplux.farmaplux.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.farmaplux.farmaplux.model.Categoria;
import com.farmaplux.farmaplux.model.Producto;
import com.farmaplux.farmaplux.repository.ProductoRepository;
import com.farmaplux.farmaplux.service.CategoriaService;
import com.farmaplux.farmaplux.service.ProductoService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Override
    public void validar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecioCompra() < 0 || producto.getPrecioVenta() < 0) {
            throw new RuntimeException("Los precios no pueden ser negativos");
        }
        if (producto.getStock() < 0 || producto.getStockMinimo() < 0) {
            throw new RuntimeException("El stock y el stock mínimo no pueden ser negativos");
        }
        normalizarCodigo(producto);
        String codigo = producto.getCodigoBarras();
        if (codigo != null) {
            Optional<Producto> otro = productoRepository.findByCodigoBarras(codigo);
            if (otro.isPresent() && !otro.get().getId().equals(producto.getId())) {
                throw new RuntimeException("Ya existe un producto con el código de barras: " + codigo);
            }
        }
    }

    @Override
    public Producto guardar(Producto producto) {
        validar(producto);
        return productoRepository.save(producto);
    }

    /** Un código vacío se guarda como null para que no choque con otros productos sin código. */
    private void normalizarCodigo(Producto producto) {
        String codigo = producto.getCodigoBarras();
        if (codigo != null) {
            codigo = codigo.trim();
            producto.setCodigoBarras(codigo.isEmpty() ? null : codigo);
        }
    }

    @Override
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Override
    public Page<Producto> listar(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public Optional<Producto> buscarPorId(String id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> buscarPorCodigoBarras(String codigo) {
        return productoRepository.findByCodigoBarras(codigo);
    }

    @Override
    public List<Producto> buscarStockBajo(int stock) {
        return productoRepository.findByStockLessThan(stock);
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public Page<Producto> buscarPorNombre(String nombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Page<Producto> buscarPorCategoria(String categoriaId, Pageable pageable) {
        Categoria categoria = categoriaService.buscarPorId(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return productoRepository.findByCategoria(categoria, pageable);
    }

    @Override
    public Page<Producto> buscarPorNombreYCategoria(String nombre, String categoriaId, Pageable pageable) {
        Categoria categoria = categoriaService.buscarPorId(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return productoRepository.findByNombreContainingIgnoreCaseAndCategoria(nombre, categoria, pageable);
    }

    @Override
    public void eliminar(String id) {
        productoRepository.deleteById(id);
    }
}
