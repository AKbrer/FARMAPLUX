package com.farmaplux.farmaplux.controller;

import com.farmaplux.farmaplux.repository.VentaRepository;
import com.farmaplux.farmaplux.service.ClienteService;
import com.farmaplux.farmaplux.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class AdminController {

    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final VentaRepository ventaRepository;

    public AdminController(ClienteService clienteService,
                           ProductoService productoService,
                           VentaRepository ventaRepository) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/admin")
    public String inicioAdmin(Model model) {
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalProductos", productoService.listar().size());

        var ventasHoy = ventaRepository.findByFecha(LocalDate.now());
        model.addAttribute("ventasHoy", ventasHoy.size());

        var mesActual = LocalDate.now().withDayOfMonth(1);
        var todas = ventaRepository.findAll();
        double ingresosMensuales = todas.stream()
                .filter(v -> !v.getFecha().isBefore(mesActual))
                .mapToDouble(v -> v.getTotal())
                .sum();
        model.addAttribute("ingresosMensuales", "$" + String.format("%.0f", ingresosMensuales));

        return "admin/inicio";
    }
}
