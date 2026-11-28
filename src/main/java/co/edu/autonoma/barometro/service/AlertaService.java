package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.AlertaResponse;
import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.dto.OpcionTutoria;
import co.edu.autonoma.barometro.model.Alerta;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.NivelRiesgo;
import co.edu.autonoma.barometro.repository.AlertaRepository;
import co.edu.autonoma.barometro.repository.CalendarioAcademico;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Genera y consulta las alertas de riesgo (RF-05), con las reglas del documento de la fórmula.
 *
 * <p>La alerta se crea después de registrar una nota, solo si la materia tiene información
 * completa (RN-01, CA-05.7) y queda en Nublado o Tormenta. Va para el corte en curso, que
 * es el primero sin notas, y hay como máximo una por materia y corte. Sale en cuanto
 * aparece el riesgo, aunque falten menos de 14 días para el cierre, y muestra los días
 * que quedan (CA-05.3).
 */
@Service
public class AlertaService {

    private static final Long ESTUDIANTE_PRUEBA_ID = 1L;
    private static final String RUTA_TUTORIAS = "/api/tutorias";

    private final EstadoMateriaService estadoMateriaService;
    private final MateriaRepository materiaRepository;
    private final AlertaRepository alertaRepository;
    private final CalendarioAcademico calendario;
    private final Clock reloj;

    public AlertaService(EstadoMateriaService estadoMateriaService, MateriaRepository materiaRepository,
                         AlertaRepository alertaRepository, CalendarioAcademico calendario, Clock reloj) {
        this.estadoMateriaService = estadoMateriaService;
        this.materiaRepository = materiaRepository;
        this.alertaRepository = alertaRepository;
        this.calendario = calendario;
        this.reloj = reloj;
    }

    /**
     * Revisa el riesgo de la materia y crea la alerta si corresponde.
     *
     * @return la alerta creada, o vacío si no hacía falta una nueva
     */
    public Optional<Alerta> generarSiHayRiesgo(Long materiaId) {
        EstadoMateriaResponse estado = estadoMateriaService.consultar(materiaId);
        if (!EstadoMateriaService.COMPLETA.equals(estado.estado())
                || CalculadoraRiesgo.DESPEJADO.equals(estado.nivelRiesgo())) {
            return Optional.empty();
        }

        Optional<Integer> corteEnCurso = estadoMateriaService.corteEnCurso(materiaId);
        if (corteEnCurso.isEmpty() || alertaRepository.existePorMateriaYCorte(materiaId, corteEnCurso.get())) {
            return Optional.empty();
        }

        int corte = corteEnCurso.get();
        LocalDate hoy = LocalDate.now(reloj);
        Alerta alerta = new Alerta(null, materiaId, corte, NivelRiesgo.valueOf(estado.nivelRiesgo()),
                estado.notaNecesaria(), LocalDateTime.now(reloj),
                calendario.fechaCierre(corte), calendario.diasParaCierre(corte, hoy));
        return Optional.of(alertaRepository.guardar(alerta));
    }

    /** Alertas del estudiante, de la más reciente a la más antigua (GET /api/alertas). */
    public List<AlertaResponse> listar() {
        Map<Long, Materia> materias = materiaRepository.listarPorEstudiante(ESTUDIANTE_PRUEBA_ID).stream()
                .collect(Collectors.toMap(Materia::getId, Function.identity()));

        return alertaRepository.listarPorMaterias(materias.keySet()).stream()
                .map(alerta -> aRespuesta(alerta, materias.get(alerta.getIdMateria()).getNombre()))
                .toList();
    }

    private AlertaResponse aRespuesta(Alerta alerta, String nombreMateria) {
        String mensaje = "Necesitas " + String.format(Locale.ROOT, "%.2f", alerta.getNotaNecesaria())
                + " en el corte " + alerta.getCorte() + " para aprobar " + nombreMateria + ".";
        return new AlertaResponse(alerta.getId(), alerta.getIdMateria(), nombreMateria, alerta.getCorte(),
                alerta.getNivelRiesgo().name(), alerta.getNotaNecesaria(), alerta.getFechaGeneracion(),
                alerta.getFechaCierreCorte(), alerta.getDiasParaCierre(), mensaje,
                new OpcionTutoria(true, RUTA_TUTORIAS), EstadoMateriaService.AVISO);
    }
}
