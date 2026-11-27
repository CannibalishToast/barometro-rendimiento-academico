package co.edu.autonoma.barometro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Datos para registrar una materia: POST /api/materias (RF-01, CA-01.1 y CA-01.2).
 */
public record MateriaRequest(
        @NotBlank(message = "Falta el nombre de la materia.")
        String nombre,

        @NotBlank(message = "Falta el docente de la materia.")
        String docente,

        @NotNull(message = "Falta el horario de la materia.")
        @Valid
        HorarioDto horario,

        @NotBlank(message = "Falta el salón de la materia.")
        String salon
) {
}
