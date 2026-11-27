package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.MateriaPronostico;
import co.edu.autonoma.barometro.dto.PronosticoResponse;
import co.edu.autonoma.barometro.dto.ResumenPronostico;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PronosticoServiceTest {

    private MateriaRepository materiaRepository;
    private NotaRepository notaRepository;
    private PronosticoService service;

    @BeforeEach
    void preparar() {
        materiaRepository = new MateriaRepository();
        notaRepository = new NotaRepository();
        EstadoMateriaService estadoMateriaService =
                new EstadoMateriaService(materiaRepository, notaRepository, new AlertaRepository());
        service = new PronosticoService(materiaRepository, estadoMateriaService);
    }

    // Crea una materia del estudiante indicado y devuelve su id
    private Long materia(Long idEstudiante, String nombre) {
        return materiaRepository.guardar(new Materia(null, idEstudiante, nombre, "Profe",
                List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(10, 0), "Salón 1")).getId();
    }

    // Guarda una nota con el 100% del corte
    private void notaDelCorte(Long materiaId, int corte, double valor) {
        notaRepository.guardar(new Nota(null, materiaId, corte, "Parcial", valor, 100.0, null));
    }

    @Test
    void juntaElRiesgoDeCadaMateriaYCuentaPorNivel() {
        Long despejada = materia(1L, "Cálculo");
        notaDelCorte(despejada, 1, 3.0);
        notaDelCorte(despejada, 2, 2.5);

        Long nublada = materia(1L, "Álgebra Lineal");
        notaDelCorte(nublada, 1, 2.5);
        notaDelCorte(nublada, 2, 2.5);

        Long tormenta = materia(1L, "Física");
        notaDelCorte(tormenta, 1, 1.0);
        notaDelCorte(tormenta, 2, 1.0);

        materia(1L, "Química");

        PronosticoResponse pronostico = service.consultar();

        assertEquals(4, pronostico.materias().size());
        assertEquals(new ResumenPronostico(1, 1, 1, 1), pronostico.resumen());
        assertEquals(EstadoMateriaService.AVISO, pronostico.aviso());

        MateriaPronostico algebra = pronostico.materias().get(1);
        assertEquals("Álgebra Lineal", algebra.materia());
        assertEquals(EstadoMateriaService.COMPLETA, algebra.estado());
        assertEquals(CalculadoraRiesgo.NUBLADO, algebra.nivelRiesgo());
        assertEquals(3.79, algebra.notaNecesaria(), 0.000001);
    }

    @Test
    void materiaIncompletaNoTieneNivelNiNotaNecesaria() {
        Long incompleta = materia(1L, "Química");
        notaRepository.guardar(new Nota(null, incompleta, 1, "Quiz", 2.0, 60.0, null));

        MateriaPronostico quimica = service.consultar().materias().get(0);

        assertEquals(EstadoMateriaService.INCOMPLETA, quimica.estado());
        assertNull(quimica.nivelRiesgo());
        assertNull(quimica.notaNecesaria());
    }

    @Test
    void noIncluyeMateriasDeOtroEstudiante() {
        materia(1L, "Cálculo");
        materia(2L, "Física de otro");

        PronosticoResponse pronostico = service.consultar();

        assertEquals(1, pronostico.materias().size());
        assertEquals("Cálculo", pronostico.materias().get(0).materia());
    }

    @Test
    void sinMateriasElPronosticoEstaVacioYConAviso() {
        PronosticoResponse pronostico = service.consultar();

        assertTrue(pronostico.materias().isEmpty());
        assertEquals(new ResumenPronostico(0, 0, 0, 0), pronostico.resumen());
        assertEquals(EstadoMateriaService.AVISO, pronostico.aviso());
    }
}
