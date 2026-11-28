package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.CorteFaltante;
import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.exception.SinPermisoException;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import co.edu.autonoma.barometro.repository.AlertaRepository;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoMateriaServiceTest {

    private NotaRepository notaRepository;
    private EstadoMateriaService service;
    private Long materiaId;
    private Long materiaAjenaId;

    // Antes de cada prueba: repositorios nuevos, una materia sin notas y otra de otro estudiante
    @BeforeEach
    void preparar() {
        MateriaRepository materiaRepository = new MateriaRepository();
        notaRepository = new NotaRepository();
        service = new EstadoMateriaService(materiaRepository, notaRepository, new AlertaRepository());

        Materia materia = materiaRepository.guardar(new Materia(null, 1L, "Álgebra", "Profe Gómez",
                List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(10, 0), "Salón 204"));
        materiaId = materia.getId();

        Materia ajena = materiaRepository.guardar(new Materia(null, 2L, "Física", "Profe Ruiz",
                List.of("Martes"), LocalTime.of(10, 0), LocalTime.of(12, 0), "Salón 101"));
        materiaAjenaId = ajena.getId();
    }

    // Guarda una nota de 4.0 en el corte y con el porcentaje indicados
    private void nota(int corte, double porcentaje) {
        notaRepository.guardar(new Nota(null, materiaId, corte, "Parcial", 4.0, porcentaje, null));
    }
    
    // Guarda una nota con el valor, el corte y el porcentaje indicados
    private void nota(int corte, double valor, double porcentaje) {
        notaRepository.guardar(new Nota(null, materiaId, corte, "Parcial", valor, porcentaje, null));
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
    void materiaCompletaCalculaElRiesgoConLasNotasDeCadaCorte() {
        nota(1, 3.5, 60);
        nota(1, 2.25, 40);
        nota(2, 2.5, 100);

        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(EstadoMateriaService.COMPLETA, estado.estado());
        assertEquals(1.775, estado.notaAcumulada(), 0.000001);
        assertEquals(35.0, estado.porcentajeRestante(), 0.000001);
        assertEquals(3.36, estado.notaNecesaria(), 0.000001);
        assertEquals(CalculadoraRiesgo.DESPEJADO, estado.nivelRiesgo());
        assertFalse(estado.alertaGenerada());
    }

    @Test
    void materiaIncompletaNoCalculaRiesgo() {
        nota(1, 2.0, 60);

        EstadoMateriaResponse estado = service.consultar(materiaId);

        assertEquals(EstadoMateriaService.INCOMPLETA, estado.estado());
        assertNull(estado.nivelRiesgo());
        assertNull(estado.notaAcumulada());
        assertNull(estado.porcentajeRestante());
        assertNull(estado.notaNecesaria());
    }

    @Test
    void materiaQueNoExisteLanzaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.consultar(99L));
    }

    @Test
    void estadoDeMateriaDeOtroEstudianteLanzaSinPermiso() {
        SinPermisoException error = assertThrows(SinPermisoException.class,
                () -> service.consultar(materiaAjenaId));

        assertEquals("Solo el estudiante dueño de la materia puede consultar su estado.",
                error.getMessage());
    }
}
