package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.AlertaResponse;
import co.edu.autonoma.barometro.dto.NotaRequest;
import co.edu.autonoma.barometro.model.Alerta;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.NivelRiesgo;
import co.edu.autonoma.barometro.repository.AlertaRepository;
import co.edu.autonoma.barometro.repository.CalendarioAcademico;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la generación de alertas (RF-05) con repositorios reales en memoria.
 * El reloj está fijo en el 10 de noviembre de 2026 a las 9:30, así que el corte 3,
 * que cierra el 28 de noviembre, está a 18 días.
 */
class AlertaServiceTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 10, 9, 30);

    private MateriaRepository materiaRepository;
    private NotaRepository notaRepository;
    private AlertaRepository alertaRepository;
    private EstadoMateriaService estadoMateriaService;
    private AlertaService alertaService;
    private NotaService notaService;
    private Long materiaId;

    @BeforeEach
    void preparar() {
        prepararConReloj(AHORA);
    }

    // Arma todos los servicios con repositorios nuevos y el reloj fijo en la fecha indicada
    private void prepararConReloj(LocalDateTime ahora) {
        materiaRepository = new MateriaRepository();
        notaRepository = new NotaRepository();
        alertaRepository = new AlertaRepository();
        estadoMateriaService = new EstadoMateriaService(materiaRepository, notaRepository, alertaRepository);
        Clock reloj = Clock.fixed(ahora.atZone(ZONA).toInstant(), ZONA);
        CalendarioAcademico calendario = new CalendarioAcademico("2026-09-05", "2026-10-17", "2026-11-28");
        alertaService = new AlertaService(estadoMateriaService, materiaRepository, alertaRepository,
                calendario, reloj);
        notaService = new NotaService(materiaRepository, notaRepository, alertaService);

        Materia materia = materiaRepository.guardar(new Materia(null, 1L, "Álgebra Lineal", "Profe Gómez",
                List.of("Lunes"), LocalTime.of(8, 0), LocalTime.of(10, 0), "Salón 204"));
        materiaId = materia.getId();
    }

    // Registra una nota por el flujo real de POST notas (NotaService)
    private void nota(int corte, double valor, double porcentaje) {
        notaService.registrar(materiaId, new NotaRequest(corte, "Parcial", valor, porcentaje));
    }

    @Test
    void materiaEnNubladoGeneraAlertaParaElCorteEnCurso() {
        nota(1, 2.5, 100);
        nota(2, 2.5, 100);

        List<Alerta> alertas = alertaRepository.listarPorMateria(materiaId);

        assertEquals(1, alertas.size());
        Alerta alerta = alertas.get(0);
        assertEquals(3, alerta.getCorte());
        assertEquals(NivelRiesgo.NUBLADO, alerta.getNivelRiesgo());
        assertEquals(3.79, alerta.getNotaNecesaria(), 0.000001);
        assertEquals(AHORA, alerta.getFechaGeneracion());
        assertEquals(LocalDate.of(2026, 11, 28), alerta.getFechaCierreCorte());
        assertEquals(18, alerta.getDiasParaCierre());
    }

    @Test
    void materiaEnTormentaTambienGeneraAlerta() {
        nota(1, 1.0, 100);
        nota(2, 1.0, 100);

        Alerta alerta = alertaRepository.listarPorMateria(materiaId).get(0);

        assertEquals(NivelRiesgo.TORMENTA, alerta.getNivelRiesgo());
    }

    @Test
    void materiaEnDespejadoNoGeneraAlerta() {
        nota(1, 3.0, 100);
        nota(2, 2.5, 100);

        assertTrue(alertaRepository.listarPorMateria(materiaId).isEmpty());
    }

    @Test
    void materiaIncompletaNoGeneraAlerta() {
        nota(1, 1.0, 60);

        assertTrue(alertaRepository.listarPorMateria(materiaId).isEmpty());
    }

    @Test
    void soloHayUnaAlertaPorMateriaYCorte() {
        nota(1, 2.5, 100);
        nota(2, 2.5, 50);
        nota(2, 2.5, 50);

        assertTrue(alertaService.generarSiHayRiesgo(materiaId).isEmpty());
        assertEquals(1, alertaRepository.listarPorMateria(materiaId).size());
    }

    @Test
    void conLosTresCortesConNotasNoHayCorteEnCursoNiAlertaNueva() {
        nota(1, 2.0, 100);
        nota(2, 2.0, 100);
        nota(3, 2.0, 100);

        assertTrue(estadoMateriaService.corteEnCurso(materiaId).isEmpty());
        assertTrue(alertaService.generarSiHayRiesgo(materiaId).isEmpty());
        // Solo queda la alerta que salió cuando faltaba el corte 3
        assertEquals(1, alertaRepository.listarPorMateria(materiaId).size());
    }

    @Test
    void conMenosDeCatorceDiasLaAlertaSaleIgualYMuestraLosDias() {
        prepararConReloj(LocalDateTime.of(2026, 11, 20, 9, 30));
        nota(1, 2.5, 100);
        nota(2, 2.5, 100);

        assertEquals(8, alertaRepository.listarPorMateria(materiaId).get(0).getDiasParaCierre());
    }

    @Test
    void elEstadoDeLaMateriaIndicaQueYaHayAlerta() {
        nota(1, 2.5, 100);
        assertFalse(estadoMateriaService.consultar(materiaId).alertaGenerada());

        nota(2, 2.5, 100);

        assertTrue(estadoMateriaService.consultar(materiaId).alertaGenerada());
    }

    @Test
    void listarTraeMateriaMensajeTutoriaYAviso() {
        nota(1, 2.5, 100);
        nota(2, 2.5, 100);

        List<AlertaResponse> alertas = alertaService.listar();

        assertEquals(1, alertas.size());
        AlertaResponse alerta = alertas.get(0);
        assertEquals("Álgebra Lineal", alerta.materia());
        assertEquals("NUBLADO", alerta.nivelRiesgo());
        assertEquals("Necesitas 3.79 en el corte 3 para aprobar Álgebra Lineal.", alerta.mensaje());
        assertTrue(alerta.tutoria().disponible());
        assertEquals("/api/tutorias", alerta.tutoria().solicitarEn());
        assertEquals(EstadoMateriaService.AVISO, alerta.aviso());
    }

    @Test
    void listarNoTraeAlertasDeMateriasDeOtroEstudiante() {
        Materia ajena = materiaRepository.guardar(new Materia(null, 2L, "Física", "Profe Ruiz",
                List.of("Martes"), LocalTime.of(10, 0), LocalTime.of(12, 0), "Salón 101"));
        alertaRepository.guardar(new Alerta(null, ajena.getId(), 3, NivelRiesgo.TORMENTA, 5.0,
                AHORA, LocalDate.of(2026, 11, 28), 18));

        assertTrue(alertaService.listar().isEmpty());
    }
}
