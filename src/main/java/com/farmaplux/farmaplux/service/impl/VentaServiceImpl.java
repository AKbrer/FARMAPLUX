package com.farmaplux.farmaplux.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmaplux.farmaplux.model.DetalleVenta;
import com.farmaplux.farmaplux.model.Producto;
import com.farmaplux.farmaplux.model.Venta;
import com.farmaplux.farmaplux.repository.VentaRepository;
import com.farmaplux.farmaplux.service.ProductoService;
import com.farmaplux.farmaplux.service.VentaService;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;

    public VentaServiceImpl(VentaRepository ventaRepository, ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.productoService = productoService;
    }

    @Override
    @Transactional
    public Venta registrar(Venta venta) {
        double total = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoService.buscarPorId(detalle.getProductoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Producto no encontrado: " + detalle.getProductoId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException(
                        "Stock insuficiente para: " + producto.getNombre()
                                + " (disponible: " + producto.getStock()
                                + ", solicitado: " + detalle.getCantidad() + ")");
            }

            detalle.setNombreProducto(producto.getNombre());
            detalle.setPrecio(producto.getPrecioVenta());
            total += detalle.getCantidad() * producto.getPrecioVenta();

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoService.guardar(producto);
        }

        venta.setTotal(total);

        if ("EFECTIVO".equals(venta.getMetodoPago())) {
            if (venta.getMontoPagado() < total) {
                throw new RuntimeException(
                        "El monto pagado ($" + String.format("%.2f", venta.getMontoPagado())
                                + ") es menor al total ($" + String.format("%.2f", total) + ")");
            }
            venta.setCambio(venta.getMontoPagado() - total);
        } else {
            venta.setMontoPagado(total);
            venta.setCambio(0);
        }

        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @Override
    public Page<Venta> listar(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public Optional<Venta> buscarPorId(String id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Page<Venta> buscarPorMetodoPago(String metodoPago, Pageable pageable) {
        return ventaRepository.findByMetodoPagoContainingIgnoreCase(metodoPago, pageable);
    }

    @Override
    public void eliminar(String id) {
        ventaRepository.deleteById(id);
    }
}
