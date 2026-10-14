package com.farmaplux.farmaplux.service;

import com.farmaplux.farmaplux.model.Usuario;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    Usuario guardar(Usuario usuario);

    List<Usuario> listar();

    Page<Usuario> listar(Pageable pageable);

    Optional<Usuario> buscarPorId(String id);

    Optional<Usuario> buscarPorCorreo(String correo);

    Page<Usuario> buscarPorNombre(String nombre, Pageable pageable);

    void eliminar(String id);
}
