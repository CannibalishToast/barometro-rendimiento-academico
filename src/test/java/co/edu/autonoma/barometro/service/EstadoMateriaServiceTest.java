package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.CorteFaltante;
import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoMateriaServiceTest {

    private NotaRepository notaRepository;
    private EstadoMateriaService service;
    private Long materiaId;

    // Antes de cada prueba: repositorios nuevos y una materia sin notas
    @BeforeEach
    void preparar() {
        MateriaRepository materiaRepository = new MateriaRepository();
        notaRepository = new NotaRepository();
        service = new EstadoMateriaService(materiaRepository, notaRepository);

        Materia materia = materiaRepository.guardar(new Materia(null, 1L, "Álgebra", "Profe Gómez",
                List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(10, 0), "Salón 204"));
        materiaId = materia.getId();
    }

    // Guarda una nota de 4.0 en el corte y con el porcentaje indicados
    private void nota(int corte, double porcentaje) {
        notaRepository.guardar(new Nota(null, materiaId, corte, "Parcial", 4.0, porcentaje, null));
    }

    @Test
    void materiaSinNotasEstaIncompleta() {
        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(EstadoMateriaService.INCOMPLETA, estado.estado());
        assertFalse(estado.alertaGenerada());
        assertEquals(EstadoMateriaService.AVISO, estado.aviso());
    }

    @Test
    void corteAMediasEstaIncompletoYDiceCuantoLleva() {
        nota(1, 100);
        nota(2, 60);

        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(EstadoMateriaService.INCOMPLETA, estado.estado());
        assertFalse(estado.alertaGenerada());
        assertEquals(List.of(new CorteFaltante(2, 60.0)), estado.faltantes());
        assertEquals("Faltan notas del corte 2 (llevas 60% de 100%). "
                + "Completa tus notas para calcular el estado.", estado.mensaje());
    }

    @Test
    void corteSinNotasSeIgnoraYLaMateriaQuedaCompleta() {
        nota(1, 70);
        nota(1, 30);

        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(EstadoMateriaService.COMPLETA, estado.estado());
        assertTrue(estado.faltantes().isEmpty());
        assertEquals(EstadoMateriaService.AVISO, estado.aviso());
    }

    @Test
    void alCompletarElCortePasaDeIncompletaACompleta() {
        nota(1, 100);
        nota(2, 60);
        assertEquals(EstadoMateriaService.INCOMPLETA, service.consultar(materiaId).estado());

        nota(2, 40);

        assertEquals(EstadoMateriaService.COMPLETA, service.consultar(materiaId).estado());
    }

    @Test
    void variosCortesIncompletosSeNombranEnElMensaje() {
        nota(1, 50);
        nota(2, 62.5);

        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(2, estado.faltantes().size());
        assertEquals("Faltan notas del corte 1 (llevas 50% de 100%) y del corte 2 "
                + "(llevas 62.5% de 100%). Completa tus notas para calcular el estado.", estado.mensaje());
    }

    @Test
    void materiaQueNoExisteLanzaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.consultar(99L));
    }
}
