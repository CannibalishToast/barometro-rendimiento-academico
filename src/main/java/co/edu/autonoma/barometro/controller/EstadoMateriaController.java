package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.service.EstadoMateriaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint del estado de una materia según el contrato (openapi.yaml): indica si la
 * información está completa o qué notas faltan (RF-04).
 */
@RestController
public class EstadoMateriaController {

    private final EstadoMateriaService estadoMateriaService;

    public EstadoMateriaController(EstadoMateriaService estadoMateriaService) {
        this.estadoMateriaService = estadoMateriaService;
    }

    @GetMapping("/api/materias/{materiaId}/estado")
    public EstadoMateriaResponse consultar(@PathVariable Long materiaId) {
        return estadoMateriaService.consultar(materiaId);
    }
}
