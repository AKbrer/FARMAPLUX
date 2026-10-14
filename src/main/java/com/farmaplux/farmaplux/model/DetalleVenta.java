package com.farmaplux.farmaplux.model;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class DetalleVenta {

    private String productoId;
    private String nombreProducto;
    private int cantidad;
    private double precio;

    public DetalleVenta(String productoId, String nombreProducto, int cantidad, double precio) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }
}
