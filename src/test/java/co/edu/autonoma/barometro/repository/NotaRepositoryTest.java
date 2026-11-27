package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del repositorio en memoria de notas (SCRUM-47, RF-02). */
class NotaRepositoryTest {

    private NotaRepository repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new NotaRepository();
    }

    private static Nota nota(Long idMateria, Integer corte, String descripcion,
                             Double valor, Double porcentaje) {
        return new Nota(null, idMateria, corte, descripcion, valor, porcentaje, null);
    }

    @Test
    void guardarAsignaIdsIncrementalesYFechaDeRegistro() {
        Nota primera = repositorio.guardar(nota(1L, 1, "Parcial 1", 3.5, 30.0));
        Nota segunda = repositorio.guardar(nota(1L, 1, "Taller 1", 4.0, 20.0));

        assertEquals(1L, primera.getId());
        assertEquals(2L, segunda.getId());
        assertNotNull(primera.getFechaRegistro());
    }

    @Test
    void guardarConservaIdYFechaSiYaLosTiene() {
        LocalDateTime fecha = LocalDateTime.of(2026, 11, 10, 8, 0);
        Nota nota = new Nota(10L, 1L, 1, "Parcial", 3.0, 30.0, fecha);

        repositorio.guardar(nota);

        assertEquals(10L, nota.getId());
        assertEquals(fecha, nota.getFechaRegistro());
    }

    @Test
    void guardarNullLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.guardar(null));
    }

    @Test
    void laNotaQuedaAsociadaASuMateriaYCorte() {
        repositorio.guardar(nota(1L, 2, "Parcial 2", 3.0, 30.0));

        Nota guardada = repositorio.listarPorMateria(1L).get(0);
        assertEquals(1L, guardada.getIdMateria());
        assertEquals(2, guardada.getCorte());
    }

    @Test
    void listarPorMateriaSoloDevuelveLasSuyas() {
        repositorio.guardar(nota(1L, 1, "A", 3.0, 30.0));
        repositorio.guardar(nota(2L, 1, "B", 4.0, 30.0));
        repositorio.guardar(nota(1L, 2, "C", 5.0, 30.0));

        assertEquals(2, repositorio.listarPorMateria(1L).size());
        assertEquals(1, repositorio.listarPorMateria(2L).size());
        assertTrue(repositorio.listarPorMateria(9L).isEmpty());
    }

    @Test
    void lasNotasSeAgrupanPorCorteEnOrden() {
        repositorio.guardar(nota(1L, 2, "Parcial 2", 3.0, 30.0));
        repositorio.guardar(nota(1L, 1, "Parcial 1", 4.0, 30.0));
        repositorio.guardar(nota(1L, 1, "Taller 1", 4.5, 20.0));

        Map<Integer, List<Nota>> agrupadas = repositorio.listarPorMateriaAgrupadasPorCorte(1L);

        assertEquals(List.of(1, 2), List.copyOf(agrupadas.keySet()));
        assertEquals(2, agrupadas.get(1).size());
        assertEquals(1, agrupadas.get(2).size());
        assertTrue(repositorio.listarPorMateriaAgrupadasPorCorte(9L).isEmpty());
    }

    @Test
    void sumaPorcentajesPorMateriaYCorte() {
        repositorio.guardar(nota(1L, 1, "A", 3.0, 30.0));
        repositorio.guardar(nota(1L, 1, "B", 4.0, 50.0));
        repositorio.guardar(nota(1L, 2, "C", 4.0, 20.0));
        repositorio.guardar(nota(2L, 1, "D", 4.0, 40.0));

        assertEquals(80.0, repositorio.sumaPorcentajes(1L, 1), 0.0001);
        assertEquals(20.0, repositorio.sumaPorcentajes(1L, 2), 0.0001);
        assertEquals(40.0, repositorio.sumaPorcentajes(2L, 1), 0.0001);
        assertEquals(0.0, repositorio.sumaPorcentajes(1L, 3), 0.0001);
    }

    @Test
    void ayudasDeValidacionDeLaNota() {
        assertTrue(nota(1L, 1, "A", 0.0, 100.0).tieneValorEnRango());
        assertTrue(nota(1L, 1, "A", 5.0, 100.0).tienePorcentajeValido());
        assertFalse(nota(1L, 1, "A", 5.1, 30.0).tieneValorEnRango());
        assertFalse(nota(1L, 1, "A", 3.0, 0.0).tienePorcentajeValido());
        assertFalse(nota(1L, 1, "A", 3.0, 101.0).tienePorcentajeValido());
        assertTrue(nota(1L, 3, "A", 3.0, 30.0).tieneCorteValido());
        assertFalse(nota(1L, 4, "A", 3.0, 30.0).tieneCorteValido());
        assertFalse(new Nota().tieneCorteValido());
    }
}
