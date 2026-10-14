package com.farmaplux.farmaplux.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farmaplux.farmaplux.model.Cliente;

public interface ClienteService {

    Cliente guardar(Cliente cliente);

    List<Cliente> listar();

    Page<Cliente> listar(Pageable pageable);

    Optional<Cliente> buscarPorId(String id);

    Page<Cliente> buscarPorNombre(String nombre, Pageable pageable);

    Page<Cliente> buscarPorNombreODocumento(String termino, Pageable pageable);

    void eliminar(String id);
}
