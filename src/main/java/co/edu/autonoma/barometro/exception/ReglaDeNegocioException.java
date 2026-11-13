package co.edu.autonoma.barometro.exception;

/**
 * Se lanza cuando un dato es válido por sí solo pero incumple una regla que depende
 * de lo ya guardado, p. ej. que el corte supere el 100%; responde 400.
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
