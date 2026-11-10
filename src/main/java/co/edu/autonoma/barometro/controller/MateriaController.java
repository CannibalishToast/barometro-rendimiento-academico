package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.MateriaRequest;
import co.edu.autonoma.barometro.dto.MateriaResponse;
import co.edu.autonoma.barometro.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de materias según el contrato (openapi.yaml): registrar y consultar (RF-01).
 */
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MateriaResponse registrar(@Valid @RequestBody MateriaRequest request) {
        return materiaService.registrar(request);
    }

    @GetMapping
    public List<MateriaResponse> listar() {
        return materiaService.listar();
    }
}
