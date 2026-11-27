package co.edu.autonoma.barometro.dto;

/**
 * Opción de pedir tutoría desde una alerta (esquema OpcionTutoria del contrato, RN-02).
 */
public record OpcionTutoria(boolean disponible, String solicitarEn) {
}
