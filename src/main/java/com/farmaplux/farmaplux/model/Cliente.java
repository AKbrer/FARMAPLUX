package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "clientes")
@Data
@NoArgsConstructor
@Builder
public class Cliente {

    @Id
    private String id;

    private String nombre;
    private String documento;
    private String telefono;
    private String correo;

    public Cliente(String id, String nombre, String documento, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.correo = correo;
    }
}
