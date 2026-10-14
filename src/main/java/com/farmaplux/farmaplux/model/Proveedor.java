package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proveedores")
@Data
@NoArgsConstructor
@Builder
public class Proveedor {

    @Id
    private String id;

    private String nombre;
    private String telefono;
    private String direccion;
    private String email;

    public Proveedor(String id, String nombre, String telefono, String direccion, String email) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
    }
}
