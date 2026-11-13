package co.edu.autonoma.barometro.dto;

/**
 * Cuerpo de las respuestas de error de la API, p. ej. {"error": "Falta el salón de la materia."}.
 */
public record ErrorResponse(String error) {
}
