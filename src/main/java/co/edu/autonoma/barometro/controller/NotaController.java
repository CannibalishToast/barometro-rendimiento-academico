package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.NotaRequest;
import co.edu.autonoma.barometro.dto.NotaResponse;
import co.edu.autonoma.barometro.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de notas según el contrato (openapi.yaml): registrar una nota
 * en un corte de una materia (RF-02).
 */
@RestController
@RequestMapping("/api/materias/{materiaId}/notas")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotaResponse registrar(@PathVariable Long materiaId,
                                  @Valid @RequestBody NotaRequest request) {
        return notaService.registrar(materiaId, request);
    }
}
