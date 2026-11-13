package co.edu.autonoma.barometro.exception;

/**
 * Se lanza cuando se pide algo que no existe, p. ej. una materia; responde 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
