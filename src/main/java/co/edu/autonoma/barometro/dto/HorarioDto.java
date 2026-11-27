package co.edu.autonoma.barometro.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

/**
 * Horario de una materia: días de clase y hora de inicio y fin (RF-01).
 */
public record HorarioDto(
        @NotEmpty(message = "Faltan los días del horario.")
        List<String> dias,

        @NotNull(message = "Falta la hora de inicio del horario.")
        LocalTime horaInicio,

        @NotNull(message = "Falta la hora de fin del horario.")
        LocalTime horaFin
) {
}
