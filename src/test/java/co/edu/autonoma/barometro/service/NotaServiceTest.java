package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.NotaRequest;
import co.edu.autonoma.barometro.dto.NotaResponse;
import co.edu.autonoma.barometro.dto.NotasPorCorteResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.exception.ReglaDeNegocioException;
import co.edu.autonoma.barometro.exception.SinPermisoException;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotaServiceTest {

    private static final Long MATERIA_QUE_NO_EXISTE = 99L;

    private NotaRepository notaRepository;
    private NotaService service;
    private Long materiaId;
    private Long materiaAjenaId;

    // Antes de cada prueba: repositorios nuevos, una materia del estudiante 1 y otra del estudiante 2
    @BeforeEach
    void preparar() {
        MateriaRepository materiaRepository = new MateriaRepository();
        notaRepository = new NotaRepository();
        service = new NotaService(materiaRepository, notaRepository);

        Materia materia = materiaRepository.guardar(new Materia(null, 1L, "Cálculo", "Profe Gómez",
                List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(10, 0), "Salón 204"));
        materiaId = materia.getId();

        Materia ajena = materiaRepository.guardar(new Materia(null, 2L, "Física", "Profe Ruiz",
                List.of("Martes"), LocalTime.of(10, 0), LocalTime.of(12, 0), "Salón 101"));
        materiaAjenaId = ajena.getId();
    }

    // Una nota de ejemplo, solo cambian el corte y el porcentaje
    private NotaRequest nota(int corte, double porcentaje) {
        return new NotaRequest(corte, "Parcial", 4.0, porcentaje);
    }

    @Test
    void registrarDevuelveLaNotaConSuId() {
        NotaResponse respuesta = service.registrar(materiaId, nota(1, 30));

        assertEquals(1L, respuesta.id());
        assertEquals(materiaId, respuesta.materiaId());
        assertEquals(1, respuesta.corte());
        assertEquals(4.0, respuesta.valor());
        assertEquals(30.0, respuesta.porcentaje());
    }

    @Test
    void registrarRechazaSiElCorteSuperaElCienPorCiento() {
        service.registrar(materiaId, nota(1, 80));

        ReglaDeNegocioException error = assertThrows(ReglaDeNegocioException.class,
                () -> service.registrar(materiaId, nota(1, 30)));

        assertEquals("Los porcentajes del corte no pueden superar el 100%. No se guardó.",
                error.getMessage());
        assertEquals(1, notaRepository.listarPorMateria(materiaId).size());
    }

    @Test
    void registrarAceptaSiElCorteLlegaJustoACien() {
        service.registrar(materiaId, nota(1, 70));
        service.registrar(materiaId, nota(1, 30));

        assertEquals(2, notaRepository.listarPorMateria(materiaId).size());
    }

    @Test
    void registrarEnMateriaQueNoExisteLanzaNoEncontrado() {
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,
                () -> service.registrar(MATERIA_QUE_NO_EXISTE, nota(1, 30)));

        assertEquals("La materia no existe.", error.getMessage());
        assertTrue(notaRepository.listarPorMateria(MATERIA_QUE_NO_EXISTE).isEmpty());
    }

    @Test
    void listarPorCorteAgrupaLasNotasYSumaSusPorcentajes() {
        service.registrar(materiaId, nota(1, 60));
        service.registrar(materiaId, nota(1, 40));
        service.registrar(materiaId, nota(2, 30));

        List<NotasPorCorteResponse> cortes = service.listarPorCorte(materiaId);

        assertEquals(2, cortes.size());
        assertEquals(1, cortes.get(0).corte());
        assertEquals(100.0, cortes.get(0).porcentajeRegistrado());
        assertEquals(2, cortes.get(0).notas().size());
        assertEquals(2, cortes.get(1).corte());
        assertEquals(30.0, cortes.get(1).porcentajeRegistrado());
    }

    @Test
    void listarPorCorteSinNotasDevuelveListaVacia() {
        assertTrue(service.listarPorCorte(materiaId).isEmpty());
    }

    @Test
    void listarPorCorteDeMateriaQueNoExisteLanzaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.listarPorCorte(MATERIA_QUE_NO_EXISTE));
    }

    @Test
    void registrarEnMateriaDeOtroEstudianteLanzaSinPermisoYNoGuarda() {
        SinPermisoException error = assertThrows(SinPermisoException.class,
                () -> service.registrar(materiaAjenaId, nota(1, 30)));

        assertEquals("Solo el estudiante dueño de la materia puede registrar notas.", error.getMessage());
        assertTrue(notaRepository.listarPorMateria(materiaAjenaId).isEmpty());
    }

    @Test
    void listarPorCorteDeMateriaDeOtroEstudianteLanzaSinPermiso() {
        assertThrows(SinPermisoException.class, () -> service.listarPorCorte(materiaAjenaId));
    }
}
