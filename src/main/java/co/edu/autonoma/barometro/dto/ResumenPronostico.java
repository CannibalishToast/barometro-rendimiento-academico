package co.edu.autonoma.barometro.dto;

/**
 * Cuántas materias hay en cada nivel de riesgo y cuántas están incompletas
 * (resumen del pronóstico general, CA-05.1).
 */
public record ResumenPronostico(int despejado, int nublado, int tormenta, int incompletas) {
}
