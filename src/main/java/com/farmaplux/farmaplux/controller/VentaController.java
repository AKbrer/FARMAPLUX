package com.farmaplux.farmaplux.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmaplux.farmaplux.model.DetalleVenta;
import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.model.Venta;
import com.farmaplux.farmaplux.service.ClienteService;
import com.farmaplux.farmaplux.service.MovimientoService;
import com.farmaplux.farmaplux.service.ProductoService;
import com.farmaplux.farmaplux.service.UsuarioService;
import com.farmaplux.farmaplux.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            model.addAttribute("page", ventaService.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", ventaService.buscarPorMetodoPago(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("venta", Venta.builder()
                .fecha(LocalDate.now())
                .detalles(new ArrayList<>())
                .metodoPago("EFECTIVO")
                .build());
        return "ventas/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Venta venta,
            @RequestParam("productoId") String[] productoIds,
            @RequestParam("cantidad") int[] cantidades,
            @RequestParam("clienteId") String clienteId,
            Authentication auth,
            RedirectAttributes redirect) {
        try {
            venta.setFecha(LocalDate.now());

            var detalles = new ArrayList<DetalleVenta>();
            for (int i = 0; i < productoIds.length; i++) {
                if (cantidades[i] > 0) {
                    detalles.add(DetalleVenta.builder()
                            .productoId(productoIds[i])
                            .cantidad(cantidades[i])
                            .build());
                }
            }
            venta.setDetalles(detalles);

            var usuario = usuarioService.buscarPorCorreo(auth.getName())
                    .orElseThrow();
            venta.setUsuario(usuario);

            var cliente = clienteService.buscarPorId(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            venta.setClientePresencial(cliente);

            Venta registrada = ventaService.registrar(venta);
            int unidades = registrada.getDetalles().stream().mapToInt(DetalleVenta::getCantidad).sum();
            movimientoService.registrar(Movimiento.VENTAS, Movimiento.CREAR, registrada.getId(),
                    "Registró la venta " + registrada.getId() + " al cliente '" + cliente.getNombre() + "'"
                            + " — " + registrada.getDetalles().size() + " producto(s), " + unidades + " unidad(es), total $"
                            + String.format("%.2f", registrada.getTotal()) + " (" + registrada.getMetodoPago() + ")");
            redirect.addFlashAttribute("success", "Venta registrada correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/ventas/nueva";
        }
        return "redirect:/ventas";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable String id, Model model) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        model.addAttribute("venta", venta);
        return "ventas/detalle";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        Venta venta = ventaService.buscarPorId(id).orElse(null);
        ventaService.eliminar(id);
        movimientoService.registrar(Movimiento.VENTAS, Movimiento.ELIMINAR, id,
                "Eliminó la venta " + id
                        + (venta != null ? " (total $" + String.format("%.2f", venta.getTotal()) + ")" : ""));
        redirect.addFlashAttribute("success", "Venta eliminada correctamente");
        return "redirect:/ventas";
    }
}
