package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.AlertaResponse;
import co.edu.autonoma.barometro.service.AlertaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint de las alertas de riesgo del estudiante según el contrato (RF-05, RN-02).
 */
@RestController
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping("/api/alertas")
    public List<AlertaResponse> listar() {
        return alertaService.listar();
    }
}
