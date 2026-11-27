package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de los datos precargados (SCRUM-38 y SCRUM-65). */
class DatosDePruebaTest {

    private MateriaRepository materias;
    private NotaRepository notas;
    private DatosDePrueba datos;

    @BeforeEach
    void preparar() {
        materias = new MateriaRepository();
        notas = new NotaRepository();
        datos = new DatosDePrueba(materias, notas);
        datos.cargar();
    }

    private List<Materia> materiasDelEstudiante() {
        return materias.listarPorEstudiante(DatosDePrueba.ESTUDIANTE_PRUEBA_ID);
    }

    @Test
    void elEstudianteDePruebaTieneIdConocido() {
        assertEquals(1L, DatosDePrueba.ESTUDIANTE_PRUEBA_ID);
    }

    @Test
    void cargaDieciseisMateriasDelEstudianteFijoConTodosSusCampos() {
        List<Materia> lista = materiasDelEstudiante();

        assertEquals(16, lista.size());
        assertEquals("Cálculo Diferencial", lista.get(0).getNombre());
        for (Materia materia : lista) {
            assertTrue(materia.tieneCamposObligatorios());
        }
    }

    @Test
    void lasNotasCubrenLosEscenariosDeLaMatriz() {
        assertTrue(notas.listarPorMateria(1L).isEmpty());
        assertEquals(List.of(1, 2),
                List.copyOf(notas.listarPorMateriaAgrupadasPorCorte(2L).keySet()));
        assertEquals(100.0, notas.sumaPorcentajes(2L, 1), 0.0001);
        assertEquals(60.0, notas.sumaPorcentajes(2L, 2), 0.0001);
        assertEquals(80.0, notas.sumaPorcentajes(3L, 1), 0.0001);
        for (int corte = 1; corte <= 3; corte++) {
            assertEquals(100.0, notas.sumaPorcentajes(4L, corte), 0.0001);
        }
        assertEquals(70.0, notas.sumaPorcentajes(5L, 1), 0.0001);
    }

    @Test
    void todasLasNotasDeEjemploSonValidas() {
        for (Materia materia : materiasDelEstudiante()) {
            for (Nota nota : notas.listarPorMateria(materia.getId())) {
                assertTrue(nota.tieneCorteValido());
                assertTrue(nota.tieneValorEnRango());
                assertTrue(nota.tienePorcentajeValido());
            }
        }
    }

    @Test
    void cargarDosVecesNoDuplicaLosDatos() {
        datos.cargar();

        assertEquals(16, materiasDelEstudiante().size());
        assertEquals(5, notas.listarPorMateria(2L).size());
    }

    @Test
    void lasMateriasDeRiesgoTienenSusCortesCompletos() {
        // id de la materia → cuántos cortes tiene con notas
        Map<Long, Integer> cortesConNotas = Map.ofEntries(
                Map.entry(6L, 2), Map.entry(7L, 2), Map.entry(8L, 2), Map.entry(9L, 2),
                Map.entry(10L, 2), Map.entry(11L, 2), Map.entry(12L, 2),
                Map.entry(13L, 3), Map.entry(14L, 3), Map.entry(15L, 3), Map.entry(16L, 3));

        cortesConNotas.forEach((idMateria, cortes) -> {
            Map<Integer, List<Nota>> agrupadas = notas.listarPorMateriaAgrupadasPorCorte(idMateria);
            assertEquals(cortes, agrupadas.size(), "cortes de la materia " + idMateria);
            for (int corte = 1; corte <= cortes; corte++) {
                assertEquals(100.0, notas.sumaPorcentajes(idMateria, corte), 0.0001,
                        "materia " + idMateria + ", corte " + corte);
            }
        });
    }

    @Test
    void lasMateriasDeRiesgoTienenLasNotasDelDocumento() {
        assertEquals(List.of(3.0, 2.5), notaDeCadaCorte(6L));            // ejemplo A
        assertEquals(List.of(2.5, 2.5), notaDeCadaCorte(7L));            // ejemplo B
        assertEquals(List.of(1.9, 3.3), notaDeCadaCorte(8L));            // límite 3.5
        assertEquals(List.of(0.5, 3.5), notaDeCadaCorte(9L));            // límite 4.5
        assertEquals(List.of(0.5, 3.0), notaDeCadaCorte(10L));           // 5.0
        assertEquals(List.of(1.5, 2.0), notaDeCadaCorte(11L));           // aprobar imposible
        assertEquals(List.of(5.0, 5.0), notaDeCadaCorte(12L));           // ya aprobó
        assertEquals(List.of(3.0, 3.0, 3.0), notaDeCadaCorte(13L));      // final 3.0
        assertEquals(List.of(2.6, 3.1, 3.1), notaDeCadaCorte(14L));      // final 2.95
        assertEquals(List.of(2.8, 3.0, 3.0), notaDeCadaCorte(15L));      // final 2.94
        assertEquals(List.of(2.9, 2.9, 2.9), notaDeCadaCorte(16L));      // final 2.9
    }

    private List<Double> notaDeCadaCorte(Long idMateria) {
        return notas.listarPorMateriaAgrupadasPorCorte(idMateria).values().stream()
                .map(notasDelCorte -> notasDelCorte.get(0).getValor())
                .toList();
    }
}
