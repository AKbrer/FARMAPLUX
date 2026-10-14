package com.farmaplux.farmaplux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.farmaplux.farmaplux.model.Producto;
import com.farmaplux.farmaplux.service.CategoriaService;
import com.farmaplux.farmaplux.service.ProductoService;

@Controller
public class CatalogoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/catalogo")
    public String catalogo(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoriaId) {

        Page<Producto> productos;

        if (nombre != null && !nombre.isBlank()) {
            if (categoriaId != null && !categoriaId.isBlank()) {
                productos = productoService.buscarPorNombreYCategoria(nombre, categoriaId, PageRequest.of(page, size));
            } else {
                productos = productoService.buscarPorNombre(nombre, PageRequest.of(page, size));
            }
        } else if (categoriaId != null && !categoriaId.isBlank()) {
            productos = productoService.buscarPorCategoria(categoriaId, PageRequest.of(page, size));
        } else {
            productos = productoService.listar(PageRequest.of(page, size));
        }

        model.addAttribute("page", productos);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("nombre", nombre);
        model.addAttribute("categoriaId", categoriaId);
        return "catalogo/index";
    }
}
