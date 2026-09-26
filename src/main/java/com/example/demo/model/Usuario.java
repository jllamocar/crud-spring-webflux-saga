package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("usuarios")
public class Usuario {
    @Id
    private Long id;
    private String nombre;
    private String email;

    // Constructor personalizado si solo quieres pasar nombre y email (opcional)
    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

}
