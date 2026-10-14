package com.farmaplux.farmaplux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmaplux.farmaplux.model.Cliente;
import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.service.ClienteService;
import com.farmaplux.farmaplux.service.MovimientoService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

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
            model.addAttribute("page", clienteService.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", clienteService.buscarPorNombreODocumento(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        try {
            Cliente guardado = clienteService.guardar(cliente);
            movimientoService.registrar(Movimiento.CLIENTES, Movimiento.CREAR, guardado.getId(),
                    "Creó el cliente '" + guardado.getNombre() + "' (documento: " + guardado.getDocumento() + ")");
            redirect.addFlashAttribute("success", "Cliente guardado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes/nuevo";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id).orElseThrow();
        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Cliente cliente, RedirectAttributes redirect) {
        try {
            Cliente antes = clienteService.buscarPorId(cliente.getId()).orElse(null);
            Cliente guardado = clienteService.guardar(cliente);
            String desc = "Editó el cliente '" + guardado.getNombre() + "'";
            if (antes != null && !java.util.Objects.equals(antes.getNombre(), guardado.getNombre())) {
                desc += " (antes: '" + antes.getNombre() + "')";
            }
            movimientoService.registrar(Movimiento.CLIENTES, Movimiento.EDITAR, guardado.getId(), desc);
            redirect.addFlashAttribute("success", "Cliente actualizado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes/editar/" + cliente.getId();
        }
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        clienteService.eliminar(id);
        movimientoService.registrar(Movimiento.CLIENTES, Movimiento.ELIMINAR, id,
                "Eliminó el cliente '" + (cliente != null ? cliente.getNombre() : id) + "'");
        redirect.addFlashAttribute("success", "Cliente eliminado correctamente");
        return "redirect:/clientes";
    }
}
