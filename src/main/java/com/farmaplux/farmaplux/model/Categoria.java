package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categorias")
@Data
@NoArgsConstructor
@Builder
public class Categoria {

    @Id
    private String id;

    private String nombre;
    private String descripcion;

    public Categoria(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
