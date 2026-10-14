package com.farmaplux.farmaplux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.model.Proveedor;
import com.farmaplux.farmaplux.service.MovimientoService;
import com.farmaplux.farmaplux.service.ProveedorService;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            model.addAttribute("page", proveedorService.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", proveedorService.buscarPorNombre(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "proveedores/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, RedirectAttributes redirect) {
        try {
            Proveedor guardado = proveedorService.guardar(proveedor);
            movimientoService.registrar(Movimiento.PROVEEDORES, Movimiento.CREAR, guardado.getId(),
                    "Creó el proveedor '" + guardado.getNombre() + "'");
            redirect.addFlashAttribute("success", "Proveedor guardado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/proveedores/nuevo";
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Proveedor proveedor = proveedorService.buscarPorId(id).orElseThrow();
        model.addAttribute("proveedor", proveedor);
        return "proveedores/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Proveedor proveedor, RedirectAttributes redirect) {
        try {
            Proveedor antes = proveedorService.buscarPorId(proveedor.getId()).orElse(null);
            Proveedor guardado = proveedorService.guardar(proveedor);
            String desc = "Editó el proveedor '" + guardado.getNombre() + "'";
            if (antes != null && !java.util.Objects.equals(antes.getNombre(), guardado.getNombre())) {
                desc += " (antes: '" + antes.getNombre() + "')";
            }
            movimientoService.registrar(Movimiento.PROVEEDORES, Movimiento.EDITAR, guardado.getId(), desc);
            redirect.addFlashAttribute("success", "Proveedor actualizado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/proveedores/editar/" + proveedor.getId();
        }
        return "redirect:/proveedores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        Proveedor proveedor = proveedorService.buscarPorId(id).orElse(null);
        proveedorService.eliminar(id);
        movimientoService.registrar(Movimiento.PROVEEDORES, Movimiento.ELIMINAR, id,
                "Eliminó el proveedor '" + (proveedor != null ? proveedor.getNombre() : id) + "'");
        redirect.addFlashAttribute("success", "Proveedor eliminado correctamente");
        return "redirect:/proveedores";
    }
}
