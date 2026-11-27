package co.edu.autonoma.barometro.dto;

/**
 * Riesgo de una materia dentro del pronóstico general (esquema Pronostico del contrato).
 * nivelRiesgo y notaNecesaria van en null si la información está incompleta (RN-01).
 */
public record MateriaPronostico(
        Long materiaId,
        String materia,
        String estado,
        String nivelRiesgo,
        Double notaNecesaria
) {
}
