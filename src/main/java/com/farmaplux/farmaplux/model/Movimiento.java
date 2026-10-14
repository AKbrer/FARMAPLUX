package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Registro de auditoría de las acciones realizadas desde la vista admin.
 * Se guarda en la colección "movimientos".
 */
@Document(collection = "movimientos")
@Data
@NoArgsConstructor
@Builder
public class Movimiento {

    // Módulos
    public static final String PRODUCTOS = "PRODUCTOS";
    public static final String CATEGORIAS = "CATEGORIAS";
    public static final String PROVEEDORES = "PROVEEDORES";
    public static final String CLIENTES = "CLIENTES";
    public static final String USUARIOS = "USUARIOS";
    public static final String VENTAS = "VENTAS";

    // Acciones
    public static final String CREAR = "CREAR";
    public static final String EDITAR = "EDITAR";
    public static final String ELIMINAR = "ELIMINAR";

    @Id
    private String id;

    @Indexed
    private LocalDateTime fecha;

    private String usuarioNombre;
    private String usuarioCorreo;

    @Indexed
    private String modulo;

    private String accion;

    /** Id del registro afectado (producto, venta, etc.). */
    private String entidadId;

    private String descripcion;

    public Movimiento(String id, LocalDateTime fecha, String usuarioNombre, String usuarioCorreo, String modulo, String accion, String entidadId, String descripcion) {
        this.id = id;
        this.fecha = fecha;
        this.usuarioNombre = usuarioNombre;
        this.usuarioCorreo = usuarioCorreo;
        this.modulo = modulo;
        this.accion = accion;
        this.entidadId = entidadId;
        this.descripcion = descripcion;
    }
}
