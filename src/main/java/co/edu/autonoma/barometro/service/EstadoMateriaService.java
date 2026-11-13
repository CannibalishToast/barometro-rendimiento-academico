package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.CorteFaltante;
import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.exception.SinPermisoException;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.model.Nota;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Decide si una materia tiene la información completa para calcular su estado (RF-04).
 *
 * <p>Regla: cada corte con notas debe sumar 100%; un corte sin notas se ignora y una
 * materia sin ninguna nota está incompleta. Con información incompleta no se calcula
 * riesgo ni se genera alerta (RN-01, RNF-03).
 */
@Service
public class EstadoMateriaService {

    public static final String INCOMPLETA = "INFORMACION_INCOMPLETA";
    public static final String COMPLETA = "COMPLETA";
    public static final String AVISO =
            "Este sistema no es la fuente oficial de notas. Verifica en intraUAM.";

    private static final Long ESTUDIANTE_PRUEBA_ID = 1L;
    private static final double PORCENTAJE_COMPLETO = 100.0;
    private static final double TOLERANCIA = 0.000001;

    private final MateriaRepository materiaRepository;
    private final NotaRepository notaRepository;

    public EstadoMateriaService(MateriaRepository materiaRepository, NotaRepository notaRepository) {
        this.materiaRepository = materiaRepository;
        this.notaRepository = notaRepository;
    }

    public EstadoMateriaResponse consultar(Long materiaId) {
        verificarAcceso(materiaId, "Solo el estudiante dueño de la materia puede consultar su estado.");

        Map<Integer, List<Nota>> notasPorCorte =
                notaRepository.listarPorMateriaAgrupadasPorCorte(materiaId);

        if (notasPorCorte.isEmpty()) {
            return new EstadoMateriaResponse(materiaId, INCOMPLETA, false, List.of(),
                    "Aún no tienes notas en esta materia. Completa tus notas para calcular el estado.",
                    AVISO);
        }

        List<CorteFaltante> faltantes = notasPorCorte.keySet().stream()
                .map(corte -> new CorteFaltante(corte, notaRepository.sumaPorcentajes(materiaId, corte)))
                .filter(corte -> corte.porcentajeRegistrado() < PORCENTAJE_COMPLETO - TOLERANCIA)
                .toList();

        if (faltantes.isEmpty()) {
            return new EstadoMateriaResponse(materiaId, COMPLETA, false, List.of(),
                    "La información de la materia está completa.", AVISO);
        }

        return new EstadoMateriaResponse(materiaId, INCOMPLETA, false, faltantes,
                mensajeDeFaltantes(faltantes), AVISO);
    }

    private void verificarAcceso(Long materiaId, String mensajeSinPermiso) {
        Materia materia = materiaRepository.buscarPorId(materiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La materia no existe."));
        if (!ESTUDIANTE_PRUEBA_ID.equals(materia.getIdEstudiante())) {
            throw new SinPermisoException(mensajeSinPermiso);
        }
    }

    private String mensajeDeFaltantes(List<CorteFaltante> faltantes) {
        String detalle = faltantes.stream()
                .map(f -> "del corte " + f.corte() + " (llevas "
                        + sinDecimalesSobrantes(f.porcentajeRegistrado()) + "% de 100%)")
                .collect(Collectors.joining(" y "));
        return "Faltan notas " + detalle + ". Completa tus notas para calcular el estado.";
    }

    // 60.0 se muestra como "60" y 62.5 como "62.5"
    private String sinDecimalesSobrantes(double porcentaje) {
        if (porcentaje == Math.rint(porcentaje)) {
            return String.valueOf((long) porcentaje);
        }
        return String.valueOf(porcentaje);
    }
}
