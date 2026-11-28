package co.edu.autonoma.barometro.dto;

import java.util.List;

/**
 * Pronóstico general del estudiante (esquema Pronostico del contrato, RF-05, CA-05.1):
 * el riesgo de cada materia, el resumen por nivel y el aviso de fuente no oficial (RN-03).
 */
public record PronosticoResponse(
        List<MateriaPronostico> materias,
        ResumenPronostico resumen,
        String aviso
) {
}
