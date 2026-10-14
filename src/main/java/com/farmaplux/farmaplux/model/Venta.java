package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "ventas")
@Data
@NoArgsConstructor
@Builder
public class Venta {

    @Id
    private String id;

    private LocalDate fecha;

    private Usuario usuario;

    private List<DetalleVenta> detalles;

    private double total;

    private Cliente clientePresencial;

    private String metodoPago;

    private double montoPagado;

    private double cambio;

    public Venta(String id, LocalDate fecha, Usuario usuario, List<DetalleVenta> detalles, double total, Cliente clientePresencial, String metodoPago, double montoPagado, double cambio) {
        this.id = id;
        this.fecha = fecha;
        this.usuario = usuario;
        this.detalles = detalles;
        this.total = total;
        this.clientePresencial = clientePresencial;
        this.metodoPago = metodoPago;
        this.montoPagado = montoPagado;
        this.cambio = cambio;
    }
}
