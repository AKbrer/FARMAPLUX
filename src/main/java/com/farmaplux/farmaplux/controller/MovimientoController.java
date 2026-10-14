package com.farmaplux.farmaplux.controller;

import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String modulo) {

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        model.addAttribute("page", movimientoService.buscar(q, modulo, pageable));
        model.addAttribute("q", q);
        model.addAttribute("modulo", modulo);
        model.addAttribute("modulos", List.of(
                Movimiento.PRODUCTOS, Movimiento.CATEGORIAS, Movimiento.PROVEEDORES,
                Movimiento.CLIENTES, Movimiento.USUARIOS, Movimiento.VENTAS));
        return "movimientos/lista";
    }
}
