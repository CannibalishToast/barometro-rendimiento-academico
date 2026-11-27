package co.edu.autonoma.barometro.dto;

/**
 * Un corte al que le faltan notas: cuánto lleva registrado de 100% (RF-04, CA-04.7).
 */
public record CorteFaltante(
        Integer corte,
        Double porcentajeRegistrado
) {
}
