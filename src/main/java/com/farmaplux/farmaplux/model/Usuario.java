package com.farmaplux.farmaplux.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@Data
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    private String id;

    private String nombre;
    private String correo;
    private String password;
    private String rol; // ADMIN o CLIENTE

    public Usuario(String id, String nombre, String correo, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }
}
