package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.PronosticoResponse;
import co.edu.autonoma.barometro.service.PronosticoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint del pronóstico general del estudiante según el contrato (RF-05, CA-05.1).
 */
@RestController
public class PronosticoController {

    private final PronosticoService pronosticoService;

    public PronosticoController(PronosticoService pronosticoService) {
        this.pronosticoService = pronosticoService;
    }

    @GetMapping("/api/pronostico")
    public PronosticoResponse consultar() {
        return pronosticoService.consultar();
    }
}
