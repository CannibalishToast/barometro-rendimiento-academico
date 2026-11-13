package co.edu.autonoma.barometro.dto;

import java.util.List;

/**
 * Notas de un corte tal como las devuelve la API (esquema NotasPorCorte del
 * contrato, RF-02, CA-02.5): el número del corte, cuánto suman sus porcentajes
 * y la lista de notas.
 */
public record NotasPorCorteResponse(
        Integer corte,
        Double porcentajeRegistrado,
        List<NotaResponse> notas
) {
}
