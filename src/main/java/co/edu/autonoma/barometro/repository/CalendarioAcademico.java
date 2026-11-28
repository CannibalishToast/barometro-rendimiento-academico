package co.edu.autonoma.barometro.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Fechas de cierre de los cortes del semestre de prueba (art. 42 del Reglamento
 * Estudiantil: las notas de cada corte se reportan antes de su cierre).
 *
 * <p>Backend las usa para calcular los días que faltan para el cierre del corte en
 * curso y generar la alerta con 14 días de anticipación (CA-05.3). Son datos de
 * prueba: se pueden cambiar sin tocar el código con las propiedades
 * {@code barometro.calendario.cierre-corte-1}, {@code -2} y {@code -3}
 * (formato AAAA-MM-DD).</p>
 */
@Component
public class CalendarioAcademico {

    /** Días de anticipación con los que debe salir la alerta (CA-05.3). */
    public static final int DIAS_ANTICIPACION_ALERTA = 14;

    private final Map<Integer, LocalDate> cierres;

    public CalendarioAcademico(
            @Value("${barometro.calendario.cierre-corte-1:2026-09-05}") String cierreCorte1,
            @Value("${barometro.calendario.cierre-corte-2:2026-10-17}") String cierreCorte2,
            @Value("${barometro.calendario.cierre-corte-3:2026-11-28}") String cierreCorte3) {
        this.cierres = Map.of(
                1, LocalDate.parse(cierreCorte1),
                2, LocalDate.parse(cierreCorte2),
                3, LocalDate.parse(cierreCorte3));
    }

    /** Fecha de cierre del corte 1, 2 o 3. */
    public LocalDate fechaCierre(int corte) {
        LocalDate fecha = cierres.get(corte);
        if (fecha == null) {
            throw new IllegalArgumentException("El corte debe ser 1, 2 o 3.");
        }
        return fecha;
    }

    /** Días que faltan desde {@code hoy} hasta el cierre del corte (negativo si ya cerró). */
    public int diasParaCierre(int corte, LocalDate hoy) {
        return (int) ChronoUnit.DAYS.between(hoy, fechaCierre(corte));
    }
}
