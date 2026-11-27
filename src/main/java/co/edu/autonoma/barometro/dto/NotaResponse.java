package co.edu.autonoma.barometro.dto;

/**
 * Nota registrada tal como la devuelve la API (esquema Nota del contrato, RF-02).
 */
public record NotaResponse(
        Long id,
        Long materiaId,
        Integer corte,
        String descripcion,
        Double valor,
        Double porcentaje
) {
}
