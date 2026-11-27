package co.edu.autonoma.barometro.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Datos para registrar una nota en un corte (RF-02, CA-02.1 a CA-02.3).
 *
 * <p>La regla de que el corte no supere el 100% y la de que la materia exista
 * se revisan en el servicio, porque necesitan consultar lo que ya está guardado.
 */
public record NotaRequest(

        @NotNull(message = "Falta el corte de la nota.")
        @Min(value = 1, message = "El corte debe ser 1, 2 o 3.")
        @Max(value = 3, message = "El corte debe ser 1, 2 o 3.")
        Integer corte,

        @NotBlank(message = "Falta la descripción de la nota.")
        String descripcion,

        @NotNull(message = "Falta el valor de la nota.")
        @DecimalMin(value = "0.0", message = "La nota debe estar entre 0.0 y 5.0. No se guardó.")
        @DecimalMax(value = "5.0", message = "La nota debe estar entre 0.0 y 5.0. No se guardó.")
        Double valor,

        @NotNull(message = "Falta el porcentaje de la nota.")
        @DecimalMin(value = "0", inclusive = false,
                message = "El porcentaje debe ser mayor que 0 y hasta 100. No se guardó.")
        @DecimalMax(value = "100",
                message = "El porcentaje debe ser mayor que 0 y hasta 100. No se guardó.")
        Double porcentaje
) {
}
