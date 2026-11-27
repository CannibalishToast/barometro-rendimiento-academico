package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Alerta;
import co.edu.autonoma.barometro.model.NivelRiesgo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del repositorio en memoria de alertas (SCRUM-71, RF-05). */
class AlertaRepositoryTest {

    private AlertaRepository repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new AlertaRepository();
    }

    private static Alerta alerta(Long idMateria, Integer corte, LocalDateTime fecha) {
        return new Alerta(null, idMateria, corte, NivelRiesgo.NUBLADO, 3.79, fecha,
                LocalDate.of(2026, 11, 28), 18);
    }

    @Test
    void guardarAsignaIdsIncrementalesYFechaDeGeneracion() {
        Alerta primera = repositorio.guardar(alerta(2L, 3, null));
        Alerta segunda = repositorio.guardar(alerta(3L, 3, null));

        assertEquals(1L, primera.getId());
        assertEquals(2L, segunda.getId());
        assertNotNull(primera.getFechaGeneracion());
    }

    @Test
    void guardarConservaIdYFechaSiYaLosTiene() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 10, 9, 30);
        Alerta alerta = alerta(2L, 3, fecha);
        alerta.setId(10L);

        repositorio.guardar(alerta);

        assertEquals(10L, alerta.getId());
        assertEquals(fecha, alerta.getFechaGeneracion());
        assertSame(alerta, repositorio.buscarPorId(10L).orElseThrow());
    }

    @Test
    void guardarNullLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.guardar(null));
    }

    @Test
    void laAlertaQuedaAsociadaASuMateriaYCorte() {
        repositorio.guardar(alerta(2L, 3, null));

        Alerta guardada = repositorio.listarPorMateria(2L).get(0);
        assertEquals(2L, guardada.getIdMateria());
        assertEquals(3, guardada.getCorte());
        assertEquals(NivelRiesgo.NUBLADO, guardada.getNivelRiesgo());
        assertEquals(18, guardada.getDiasParaCierre());
    }

    @Test
    void buscarAlertaInexistenteDevuelveVacio() {
        assertTrue(repositorio.buscarPorId(99L).isEmpty());
        assertTrue(repositorio.buscarPorId(null).isEmpty());
    }

    @Test
    void listarPorMateriasSoloTraeLasDeEsasMateriasDeLaMasRecienteALaMasAntigua() {
        Alerta vieja = repositorio.guardar(alerta(2L, 2, LocalDateTime.of(2026, 10, 1, 8, 0)));
        Alerta nueva = repositorio.guardar(alerta(3L, 3, LocalDateTime.of(2026, 11, 10, 8, 0)));
        repositorio.guardar(alerta(9L, 3, LocalDateTime.of(2026, 11, 20, 8, 0)));

        assertEquals(List.of(nueva, vieja), repositorio.listarPorMaterias(List.of(2L, 3L)));
        assertTrue(repositorio.listarPorMaterias(List.of()).isEmpty());
        assertTrue(repositorio.listarPorMaterias(null).isEmpty());
    }

    @Test
    void aIgualFechaPrimeroLaDeIdMayor() {
        LocalDateTime misma = LocalDateTime.of(2026, 11, 10, 8, 0);
        Alerta primera = repositorio.guardar(alerta(2L, 2, misma));
        Alerta segunda = repositorio.guardar(alerta(2L, 3, misma));

        assertEquals(List.of(segunda, primera), repositorio.listarPorMateria(2L));
    }

    @Test
    void detectaSiYaHayAlertaParaLaMateriaYElCorte() {
        Alerta guardada = repositorio.guardar(alerta(2L, 3, null));

        assertTrue(repositorio.existePorMateriaYCorte(2L, 3));
        assertSame(guardada, repositorio.buscarPorMateriaYCorte(2L, 3).orElseThrow());
        assertFalse(repositorio.existePorMateriaYCorte(2L, 2));
        assertFalse(repositorio.existePorMateriaYCorte(3L, 3));
    }
}
