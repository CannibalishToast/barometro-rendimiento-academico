package co.edu.autonoma.barometro.dto;

/**
 * Materia que devuelve la API al registrarla o consultarla (RF-01, CA-01.3).
 */
public record MateriaResponse(
        Long id,
        String nombre,
        String docente,
        HorarioDto horario,
        String salon
) {
}
