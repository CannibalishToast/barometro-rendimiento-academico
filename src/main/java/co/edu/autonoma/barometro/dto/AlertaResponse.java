package co.edu.autonoma.barometro.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Alerta de riesgo tal como la recibe el frontend (esquema Alerta del contrato, RF-05).
 *
 * <p>Trae la materia (CA-05.4), la fecha de generación y el corte (CA-05.8), los días
 * para el cierre (CA-05.3), la opción de tutoría (CA-05.5, RN-02) y el aviso de fuente
 * no oficial (CA-05.6).
 */
public record AlertaResponse(
        Long id,
        Long materiaId,
        String materia,
        Integer corte,
        String nivelRiesgo,
        Double notaNecesaria,
        LocalDateTime fechaGeneracion,
        LocalDate fechaCierreCorte,
        Integer diasParaCierre,
        String mensaje,
        OpcionTutoria tutoria,
        String aviso
) {
}
