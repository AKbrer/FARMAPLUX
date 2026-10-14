package com.farmaplux.farmaplux.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.farmaplux.farmaplux.model.Proveedor;
import com.farmaplux.farmaplux.repository.ProveedorRepository;
import com.farmaplux.farmaplux.service.ProveedorService;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public Proveedor guardar(Proveedor proveedor) {
        if (proveedor.getId() == null
                && proveedorRepository.existsByNombreIgnoreCase(proveedor.getNombre())) {
            throw new RuntimeException("Ya existe un proveedor con el nombre: " + proveedor.getNombre());
        }
        return proveedorRepository.save(proveedor);
    }

    @Override
    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    @Override
    public Page<Proveedor> listar(Pageable pageable) {
        return proveedorRepository.findAll(pageable);
    }

    @Override
    public Optional<Proveedor> buscarPorId(String id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public Page<Proveedor> buscarPorNombre(String nombre, Pageable pageable) {
        return proveedorRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public void eliminar(String id) {
        proveedorRepository.deleteById(id);
    }
}
