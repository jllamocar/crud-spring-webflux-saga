package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long> {
    // Hereda métodos reactivos como save(), findAll(), findById(), delete()

    // Spring Data R2DBC lee el nombre y crea la consulta: SELECT * FROM usuario WHERE email = ?
    Mono<Usuario> findByEmail(String email);
}
