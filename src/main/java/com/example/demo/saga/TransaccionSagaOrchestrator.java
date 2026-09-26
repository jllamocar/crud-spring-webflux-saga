package com.example.demo.saga;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TransaccionSagaOrchestrator {

    private final UsuarioRepository usuarioRepository;

    public Mono<String> ejecutarSaga(Usuario usuario, boolean simularError) {
        // PASO 1: Guardar en la base de datos local (R2DBC)
        return usuarioRepository.save(usuario)
                .flatMap(usuarioGuardado -> {
                    // PASO 2: Simulamos un paso siguiente (ej. API externa o validación)
                    if (simularError) {
                        // Forzamos un error para probar la compensación (Rollback)
                        return Mono.error(new RuntimeException("Fallo artificial en el paso 2"));
                    }
                    return Mono.just("¡Saga completada con éxito para: " + usuarioGuardado.getNombre() + "!");
                })
                // SI ALGO FALLA, EJECUTAMOS LA COMPENSACIÓN                                                              
                .onErrorResume(error -> compensarUsuario(usuario.getEmail(), error));
    }

    private Mono<String> compensarUsuario(String email, Throwable error) {
        System.out.println("[SAGA COMPENSACIÓN] Revirtiendo cambios debido a: " + error.getMessage());

        // Compensación: Buscamos y borramos el registro que se había creado
        return usuarioRepository.findByEmail(email)
                .flatMap(usuarioEncontrado -> usuarioRepository.delete(usuarioEncontrado))
                .then(Mono.just("Saga Fallida y Revertida: " + error.getMessage()));
    }

}
