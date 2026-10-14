package com.farmaplux.farmaplux.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.farmaplux.farmaplux.model.Cliente;
import com.farmaplux.farmaplux.repository.ClienteRepository;
import com.farmaplux.farmaplux.service.ClienteService;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        if (cliente.getId() == null
                && clienteRepository.existsByDocumento(cliente.getDocumento())) {
            throw new RuntimeException("Ya existe un cliente con el documento: " + cliente.getDocumento());
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Override
    public Page<Cliente> listar(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Page<Cliente> buscarPorNombre(String nombre, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    public Page<Cliente> buscarPorNombreODocumento(String termino, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrDocumentoContainingIgnoreCase(
                termino, termino, pageable);
    }

    @Override
    public void eliminar(String id) {
        clienteRepository.deleteById(id);
    }
}
