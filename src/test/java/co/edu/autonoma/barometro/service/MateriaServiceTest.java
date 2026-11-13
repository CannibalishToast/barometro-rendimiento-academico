package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.HorarioDto;
import co.edu.autonoma.barometro.dto.MateriaRequest;
import co.edu.autonoma.barometro.dto.MateriaResponse;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MateriaServiceTest {

    private MateriaRepository repositorio;
    private MateriaService service;

    // Antes de cada prueba: repositorio nuevo y vacío, para que no se mezclen
    @BeforeEach
    void preparar() {
        repositorio = new MateriaRepository();
        service = new MateriaService(repositorio);
    }

    // Un formulario de ejemplo, para no repetirlo en cada prueba
    private MateriaRequest formulario(String nombre) {
        HorarioDto horario = new HorarioDto(
                List.of("Lunes", "Miércoles"), LocalTime.of(8, 0), LocalTime.of(10, 0));
        return new MateriaRequest(nombre, "Profe Gómez", horario, "Salón 204");
    }

    @Test
    void registrarDevuelveLaMateriaConSuId() {
        MateriaResponse respuesta = service.registrar(formulario("Cálculo"));

        assertEquals(1L, respuesta.id());
        assertEquals("Cálculo", respuesta.nombre());
        assertEquals("Profe Gómez", respuesta.docente());
        assertEquals("Salón 204", respuesta.salon());
        assertEquals(List.of("Lunes", "Miércoles"), respuesta.horario().dias());
        assertEquals(LocalTime.of(8, 0), respuesta.horario().horaInicio());
    }

    @Test
    void registrarLaGuardaEnElEstudianteDePrueba() {
        service.registrar(formulario("Cálculo"));

        List<Materia> guardadas = repositorio.listarPorEstudiante(1L);
        assertEquals(1, guardadas.size());
        assertEquals("Cálculo", guardadas.get(0).getNombre());
    }

    @Test
    void listarDevuelveLasMateriasRegistradas() {
        service.registrar(formulario("Cálculo"));
        service.registrar(formulario("Álgebra"));

        List<MateriaResponse> lista = service.listar();

        assertEquals(2, lista.size());
        assertEquals("Cálculo", lista.get(0).nombre());
        assertEquals("Álgebra", lista.get(1).nombre());
    }

    @Test
    void listarSinMateriasDevuelveListaVacia() {
        assertTrue(service.listar().isEmpty());
    }
}
