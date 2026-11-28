package co.edu.autonoma.barometro.repository;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas de las fechas de cierre de los cortes (SCRUM-71, CA-05.3). */
class CalendarioAcademicoTest {

    private final CalendarioAcademico calendario = new CalendarioAcademico(
            "2026-09-05", "2026-10-17", "2026-11-28");

    @Test
    void devuelveLaFechaDeCierreDeCadaCorte() {
        assertEquals(LocalDate.of(2026, 9, 5), calendario.fechaCierre(1));
        assertEquals(LocalDate.of(2026, 10, 17), calendario.fechaCierre(2));
        assertEquals(LocalDate.of(2026, 11, 28), calendario.fechaCierre(3));
    }

    @Test
    void calculaLosDiasParaElCierre() {
        // Ejemplo del contrato: alerta del 10 de noviembre, corte 3 cierra el 28 → 18 días.
        assertEquals(18, calendario.diasParaCierre(3, LocalDate.of(2026, 11, 10)));
        assertEquals(CalendarioAcademico.DIAS_ANTICIPACION_ALERTA,
                calendario.diasParaCierre(3, LocalDate.of(2026, 11, 14)));
        assertEquals(0, calendario.diasParaCierre(3, LocalDate.of(2026, 11, 28)));
        assertEquals(-2, calendario.diasParaCierre(3, LocalDate.of(2026, 11, 30)));
    }

    @Test
    void corteInvalidoLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> calendario.fechaCierre(4));
    }
}
