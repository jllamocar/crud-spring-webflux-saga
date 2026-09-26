package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.saga.TransaccionSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/saga")
@RequiredArgsConstructor
public class SagaController {

    private final TransaccionSagaOrchestrator sagaOrchestrator;

    @PostMapping(value = "/ejecutar", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> probarSaga(
            @RequestBody Usuario usuario,
            @RequestParam(defaultValue = "false") boolean simularError) {

        return sagaOrchestrator.ejecutarSaga(usuario, simularError);
    }
}