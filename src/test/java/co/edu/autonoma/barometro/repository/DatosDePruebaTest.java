package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de los datos precargados (SCRUM-38). */
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
    void cargaCincoMateriasDelEstudianteFijoConTodosSusCampos() {
        List<Materia> lista = materiasDelEstudiante();

        assertEquals(5, lista.size());
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

        assertEquals(5, materiasDelEstudiante().size());
        assertEquals(5, notas.listarPorMateria(2L).size());
    }
}
