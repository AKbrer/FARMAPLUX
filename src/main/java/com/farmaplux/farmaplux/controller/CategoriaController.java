package com.farmaplux.farmaplux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmaplux.farmaplux.model.Categoria;
import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.service.CategoriaService;
import com.farmaplux.farmaplux.service.MovimientoService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            model.addAttribute("page", categoriaService.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", categoriaService.buscarPorNombre(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes redirect) {
        try {
            Categoria guardada = categoriaService.guardar(categoria);
            movimientoService.registrar(Movimiento.CATEGORIAS, Movimiento.CREAR, guardada.getId(),
                    "Creó la categoría '" + guardada.getNombre() + "'");
            redirect.addFlashAttribute("success", "Categoría guardada correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias/nuevo";
        }
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id).orElseThrow();
        model.addAttribute("categoria", categoria);
        return "categorias/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Categoria categoria, RedirectAttributes redirect) {
        try {
            Categoria antes = categoriaService.buscarPorId(categoria.getId()).orElse(null);
            Categoria guardada = categoriaService.guardar(categoria);
            String desc = "Editó la categoría '" + guardada.getNombre() + "'";
            if (antes != null && !java.util.Objects.equals(antes.getNombre(), guardada.getNombre())) {
                desc += " (antes: '" + antes.getNombre() + "')";
            }
            movimientoService.registrar(Movimiento.CATEGORIAS, Movimiento.EDITAR, guardada.getId(), desc);
            redirect.addFlashAttribute("success", "Categoría actualizada correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/categorias/editar/" + categoria.getId();
        }
        return "redirect:/categorias";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        Categoria categoria = categoriaService.buscarPorId(id).orElse(null);
        categoriaService.eliminar(id);
        movimientoService.registrar(Movimiento.CATEGORIAS, Movimiento.ELIMINAR, id,
                "Eliminó la categoría '" + (categoria != null ? categoria.getNombre() : id) + "'");
        redirect.addFlashAttribute("success", "Categoría eliminada correctamente");
        return "redirect:/categorias";
    }
}
