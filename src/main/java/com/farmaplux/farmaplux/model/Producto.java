package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Document(collection = "productos")
@Data
@NoArgsConstructor
@Builder
public class Producto {

    @Id
    private String id;

    private String nombre;
    private String codigoBarras;

    private Categoria categoria;

    private Proveedor proveedor;

    private double precioCompra;
    private double precioVenta;

    private int stock;
    private int stockMinimo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVencimiento;

    private String lote;

    private String imagenUrl;

    public Producto(String id, String nombre, String codigoBarras, Categoria categoria, Proveedor proveedor, double precioCompra, double precioVenta, int stock, int stockMinimo, LocalDate fechaVencimiento, String lote, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.codigoBarras = codigoBarras;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.fechaVencimiento = fechaVencimiento;
        this.lote = lote;
        this.imagenUrl = imagenUrl;
    }
}
