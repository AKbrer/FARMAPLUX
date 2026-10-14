package com.farmaplux.farmaplux.controller;

import com.farmaplux.farmaplux.model.Categoria;
import com.farmaplux.farmaplux.model.Movimiento;
import com.farmaplux.farmaplux.model.Producto;
import com.farmaplux.farmaplux.model.Proveedor;
import com.farmaplux.farmaplux.service.CategoriaService;
import com.farmaplux.farmaplux.service.CloudinaryService;
import com.farmaplux.farmaplux.service.MovimientoService;
import com.farmaplux.farmaplux.service.ProductoService;
import com.farmaplux.farmaplux.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping
    public String listar(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String q) {
        if (q.isEmpty()) {
            model.addAttribute("page", productoService.listar(PageRequest.of(page, size)));
        } else {
            model.addAttribute("page", productoService.buscarPorNombre(q, PageRequest.of(page, size)));
        }
        model.addAttribute("q", q);
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new Producto());
        }
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("proveedores", proveedorService.listar());
        return "productos/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                          RedirectAttributes redirect) {
        try {
            resolverRelaciones(producto);
            // Se valida antes de subir la imagen para no dejar archivos huérfanos en Cloudinary
            productoService.validar(producto);
            if (imagen != null && !imagen.isEmpty()) {
                String url = cloudinaryService.uploadImage(imagen);
                producto.setImagenUrl(url);
            }
            Producto guardado = productoService.guardar(producto);
            movimientoService.registrar(Movimiento.PRODUCTOS, Movimiento.CREAR, guardado.getId(),
                    "Creó el producto '" + guardado.getNombre() + "'"
                            + " (código: " + valor(guardado.getCodigoBarras())
                            + ", stock inicial: " + guardado.getStock()
                            + ", precio venta: $" + String.format("%.2f", guardado.getPrecioVenta()) + ")");
            redirect.addFlashAttribute("success", "Producto guardado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("producto", producto);
            return "redirect:/productos/nuevo";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al subir la imagen");
            redirect.addFlashAttribute("producto", producto);
            return "redirect:/productos/nuevo";
        }
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        if (!model.containsAttribute("producto")) {
            Producto producto = productoService.buscarPorId(id).orElseThrow();
            model.addAttribute("producto", producto);
        }
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("proveedores", proveedorService.listar());
        return "productos/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute Producto producto,
                             @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                             RedirectAttributes redirect) {
        try {
            Producto existing = productoService.buscarPorId(producto.getId()).orElse(null);
            if (existing != null) {
                producto.setImagenUrl(existing.getImagenUrl());
            }
            resolverRelaciones(producto);
            productoService.validar(producto);

            boolean imagenCambiada = false;
            if (imagen != null && !imagen.isEmpty()) {
                if (existing != null && existing.getImagenUrl() != null) {
                    cloudinaryService.deleteImage(existing.getImagenUrl());
                }
                String url = cloudinaryService.uploadImage(imagen);
                producto.setImagenUrl(url);
                imagenCambiada = true;
            }

            Producto guardado = productoService.guardar(producto);

            String cambios = describirCambios(existing, guardado, imagenCambiada);
            movimientoService.registrar(Movimiento.PRODUCTOS, Movimiento.EDITAR, guardado.getId(),
                    "Editó el producto '" + guardado.getNombre() + "'"
                            + (cambios.isEmpty() ? " (sin cambios en los datos)" : ": " + cambios));
            redirect.addFlashAttribute("success", "Producto actualizado correctamente");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("producto", producto);
            return "redirect:/productos/editar/" + producto.getId();
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al subir la imagen");
            redirect.addFlashAttribute("producto", producto);
            return "redirect:/productos/editar/" + producto.getId();
        }
        return "redirect:/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        try {
            Producto producto = productoService.buscarPorId(id).orElse(null);
            if (producto != null && producto.getImagenUrl() != null) {
                cloudinaryService.deleteImage(producto.getImagenUrl());
            }
            productoService.eliminar(id);
            movimientoService.registrar(Movimiento.PRODUCTOS, Movimiento.ELIMINAR, id,
                    "Eliminó el producto '" + (producto != null ? producto.getNombre() : id) + "'"
                            + (producto != null ? " (código: " + valor(producto.getCodigoBarras())
                            + ", stock: " + producto.getStock() + ")" : ""));
            redirect.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar el producto");
        }
        return "redirect:/productos";
    }

    // ───────────────────────── helpers ─────────────────────────

    /** Sustituye categoría y proveedor del formulario por los registros reales (o null si no se eligió). */
    private void resolverRelaciones(Producto producto) {
        Categoria c = producto.getCategoria();
        producto.setCategoria(c == null || c.getId() == null || c.getId().isBlank()
                ? null
                : categoriaService.buscarPorId(c.getId()).orElse(null));

        Proveedor p = producto.getProveedor();
        producto.setProveedor(p == null || p.getId() == null || p.getId().isBlank()
                ? null
                : proveedorService.buscarPorId(p.getId()).orElse(null));
    }

    private String describirCambios(Producto antes, Producto despues, boolean imagenCambiada) {
        if (antes == null) {
            return "";
        }
        List<String> cambios = new ArrayList<>();
        cambio(cambios, "nombre", antes.getNombre(), despues.getNombre());
        cambio(cambios, "código", antes.getCodigoBarras(), despues.getCodigoBarras());
        cambio(cambios, "categoría",
                antes.getCategoria() != null ? antes.getCategoria().getNombre() : null,
                despues.getCategoria() != null ? despues.getCategoria().getNombre() : null);
        cambio(cambios, "proveedor",
                antes.getProveedor() != null ? antes.getProveedor().getNombre() : null,
                despues.getProveedor() != null ? despues.getProveedor().getNombre() : null);
        cambio(cambios, "precio compra", antes.getPrecioCompra(), despues.getPrecioCompra());
        cambio(cambios, "precio venta", antes.getPrecioVenta(), despues.getPrecioVenta());
        cambio(cambios, "stock", antes.getStock(), despues.getStock());
        cambio(cambios, "stock mínimo", antes.getStockMinimo(), despues.getStockMinimo());
        cambio(cambios, "fecha de vencimiento", antes.getFechaVencimiento(), despues.getFechaVencimiento());
        cambio(cambios, "lote", antes.getLote(), despues.getLote());
        if (imagenCambiada) {
            cambios.add("imagen reemplazada");
        }
        return String.join("; ", cambios);
    }

    private void cambio(List<String> cambios, String campo, Object antes, Object despues) {
        if (!Objects.equals(antes, despues)) {
            cambios.add(campo + ": " + valor(antes) + " → " + valor(despues));
        }
    }

    private String valor(Object o) {
        return o == null || o.toString().isBlank() ? "—" : o.toString();
    }
}
