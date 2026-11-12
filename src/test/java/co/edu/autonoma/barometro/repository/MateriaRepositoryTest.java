package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Materia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas del repositorio en memoria de materias (SCRUM-37, RF-01). */
class MateriaRepositoryTest {

    private MateriaRepository repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new MateriaRepository();
    }

    private static Materia materia(Long idEstudiante, String nombre) {
        return new Materia(null, idEstudiante, nombre, "Docente de ejemplo",
                List.of("LUNES", "MIERCOLES"), LocalTime.of(7, 0), LocalTime.of(9, 0), "A-101");
    }

    @Test
    void guardarAsignaIdsIncrementales() {
        Materia primera = repositorio.guardar(materia(1L, "Cálculo"));
        Materia segunda = repositorio.guardar(materia(1L, "Álgebra"));

        assertEquals(1L, primera.getId());
        assertEquals(2L, segunda.getId());
    }

    @Test
    void guardarConservaElIdSiYaLoTiene() {
        Materia materia = materia(1L, "Cálculo");
        materia.setId(10L);

        repositorio.guardar(materia);

        assertEquals(10L, materia.getId());
        assertSame(materia, repositorio.buscarPorId(10L).orElseThrow());
    }

    @Test
    void guardarNullLanzaError() {
        assertThrows(IllegalArgumentException.class, () -> repositorio.guardar(null));
    }

    @Test
    void laMateriaGuardadaQuedaDisponibleParaConsultar() {
        Materia guardada = repositorio.guardar(materia(1L, "Cálculo"));

        assertSame(guardada, repositorio.buscarPorId(guardada.getId()).orElseThrow());
        assertEquals(List.of(guardada), repositorio.listarPorEstudiante(1L));
    }

    @Test
    void listarPorEstudianteSoloDevuelveLasSuyasEnOrden() {
        Materia a = repositorio.guardar(materia(1L, "Cálculo"));
        repositorio.guardar(materia(2L, "Física"));
        Materia b = repositorio.guardar(materia(1L, "Álgebra"));

        assertEquals(List.of(a, b), repositorio.listarPorEstudiante(1L));
        assertEquals(1, repositorio.listarPorEstudiante(2L).size());
        assertTrue(repositorio.listarPorEstudiante(3L).isEmpty());
    }

    @Test
    void buscarMateriaInexistenteDevuelveVacio() {
        assertTrue(repositorio.buscarPorId(99L).isEmpty());
        assertTrue(repositorio.buscarPorId(null).isEmpty());
    }

    @Test
    void detectaSiFaltanCamposObligatorios() {
        assertTrue(materia(1L, "Cálculo").tieneCamposObligatorios());

        Materia sinSalon = materia(1L, "Cálculo");
        sinSalon.setSalon(" ");
        assertFalse(sinSalon.tieneCamposObligatorios());

        assertFalse(new Materia().tieneCamposObligatorios());
    }
}
