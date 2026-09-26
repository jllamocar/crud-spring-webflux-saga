package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioRepository usuarioRepository;

    // 1. CREATE: Crear un usuario
    @PostMapping
    public Mono<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // 2. READ: Listar todos los usuarios
    @GetMapping
    public Flux<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // 3. READ: Buscar por ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Usuario>> obtenerPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id).map(usuario -> ResponseEntity.ok().body(usuario)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 4. UPDATE: Actualizar usuario
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Usuario>> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetalles) {
        return usuarioRepository.findById(id).flatMap(usuarioExistente -> {
            usuarioExistente.setNombre(usuarioDetalles.getNombre());
            usuarioExistente.setEmail(usuarioDetalles.getEmail());
            return usuarioRepository.save(usuarioExistente);
        }).map(usuarioActualizado -> ResponseEntity.ok().body(usuarioActualizado)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 5. DELETE: Eliminar usuario
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminarUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id).flatMap(usuario -> usuarioRepository.delete(usuario).then(Mono.just(ResponseEntity.ok().<Void>build()))).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // TODO: Ejemplo usando Single de RxJava2 para buscar por ID
    @GetMapping("/rx/{id}")
    public Mono<Usuario> obtenerConRxJava(@PathVariable Long id) {
        // 1. Obtenemos un Mono normal del repositorio
        Mono<Usuario> usuarioMono = usuarioRepository.findById(id);

        // 2. (Opcional) Podríamos convertirlo a Single de RxJava2 y volverlo a Mono si aplicamos lógica de RxJava
        Single<Usuario> usuarioSingle = RxJava2Adapter.monoToSingle(usuarioMono);

        // 3. Retornamos convertido a Mono para que WebFlux lo maneje
        return RxJava2Adapter.singleToMono(usuarioSingle);
    }

    // TODO: Endpoint usando Observable de RxJava2
    @GetMapping("/observable")
    public Flux<Usuario> obtenerUsuariosConObservable() {
        // 1. Obtenemos un Flux tradicional del repositorio R2DBC
        Flux<Usuario> fluxOriginal = usuarioRepository.findAll();

        // 2. Convertimos el Flux de Reactor a un Observable de RxJava2
        Observable<Usuario> usuarioObservable = RxJava2Adapter.fluxToObservable(fluxOriginal);

        // 3. APLICANDO OPERADORES DE RXJAVA2 (Aquí está la magia)
        Observable<Usuario> observableProcesado = usuarioObservable
                // Operador FILTER: Filtramos solo los que tengan correo de gmail
                .filter(usuario -> usuario.getEmail() != null && usuario.getEmail().endsWith("@gmail.com"))

                // Operador MAP: Modificamos el objeto al vuelo (ponemos el nombre en mayúsculas)
                .map(usuario -> {
                    usuario.setNombre(usuario.getNombre().toUpperCase());
                    return usuario;
                });

        // 4. Convertimos el Observable de regreso a Flux para que WebFlux lo pueda emitir por HTTP
        return RxJava2Adapter.observableToFlux(observableProcesado, io.reactivex.BackpressureStrategy.BUFFER);
    }


}
