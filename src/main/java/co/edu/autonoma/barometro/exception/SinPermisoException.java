package co.edu.autonoma.barometro.exception;

/**
 * Se lanza cuando la materia no pertenece al estudiante que hace la petición
 * (RN-04, RNF-02); responde 403.
 */
public class SinPermisoException extends RuntimeException {

    public SinPermisoException(String mensaje) {
        super(mensaje);
    }
}
