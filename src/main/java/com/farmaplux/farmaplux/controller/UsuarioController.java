package com.farmaplux.farmaplux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.model.Usuario;
import com.farmaplux.farmaplux.service.MovimientoService;
import com.farmaplux.farmaplux.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            model.addAttribute("page", service.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", service.buscarPorNombre(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario, RedirectAttributes redirect) {
        try {
            Usuario guardado = service.guardar(usuario);
            movimientoService.registrar(Movimiento.USUARIOS, Movimiento.CREAR, guardado.getId(),
                    "Creó el usuario '" + guardado.getNombre() + "' (" + guardado.getCorreo()
                            + ", rol: " + guardado.getRol() + ")");
            redirect.addFlashAttribute("success", "Usuario guardado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/nuevo";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Usuario usuario = service.buscarPorId(id).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "usuarios/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Usuario usuario, RedirectAttributes redirect) {
        try {
            Usuario antes = service.buscarPorId(usuario.getId()).orElse(null);
            Usuario guardado = service.guardar(usuario);
            String desc = "Editó el usuario '" + guardado.getNombre() + "' (" + guardado.getCorreo() + ")";
            if (antes != null && !java.util.Objects.equals(antes.getRol(), guardado.getRol())) {
                desc += " — rol: " + antes.getRol() + " → " + guardado.getRol();
            }
            movimientoService.registrar(Movimiento.USUARIOS, Movimiento.EDITAR, guardado.getId(), desc);
            redirect.addFlashAttribute("success", "Usuario actualizado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/editar/" + usuario.getId();
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        Usuario usuario = service.buscarPorId(id).orElse(null);
        service.eliminar(id);
        movimientoService.registrar(Movimiento.USUARIOS, Movimiento.ELIMINAR, id,
                "Eliminó el usuario '" + (usuario != null ? usuario.getNombre() + "' (" + usuario.getCorreo() + ")" : id + "'"));
        redirect.addFlashAttribute("success", "Usuario eliminado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }
}
