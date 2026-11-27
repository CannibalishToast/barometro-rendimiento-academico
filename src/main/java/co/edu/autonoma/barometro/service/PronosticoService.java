package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.dto.MateriaPronostico;
import co.edu.autonoma.barometro.dto.PronosticoResponse;
import co.edu.autonoma.barometro.dto.ResumenPronostico;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Pronóstico general del estudiante (RF-05, CA-05.1): junta el estado y el riesgo de
 * todas sus materias y cuenta cuántas hay en cada nivel.
 *
 * <p>No calcula nada nuevo: reutiliza {@link EstadoMateriaService}, así el pronóstico
 * siempre coincide con lo que muestra cada materia. Las materias incompletas se cuentan
 * aparte y no tienen nivel (RN-01).
 */
@Service
public class PronosticoService {

    private static final Long ESTUDIANTE_PRUEBA_ID = 1L;

    private final MateriaRepository materiaRepository;
    private final EstadoMateriaService estadoMateriaService;

    public PronosticoService(MateriaRepository materiaRepository, EstadoMateriaService estadoMateriaService) {
        this.materiaRepository = materiaRepository;
        this.estadoMateriaService = estadoMateriaService;
    }

    public PronosticoResponse consultar() {
        List<MateriaPronostico> materias = materiaRepository.listarPorEstudiante(ESTUDIANTE_PRUEBA_ID).stream()
                .map(this::aPronostico)
                .toList();
        return new PronosticoResponse(materias, resumir(materias), EstadoMateriaService.AVISO);
    }

    private MateriaPronostico aPronostico(Materia materia) {
        EstadoMateriaResponse estado = estadoMateriaService.consultar(materia.getId());
        return new MateriaPronostico(materia.getId(), materia.getNombre(), estado.estado(),
                estado.nivelRiesgo(), estado.notaNecesaria());
    }

    private ResumenPronostico resumir(List<MateriaPronostico> materias) {
        return new ResumenPronostico(
                contarNivel(materias, CalculadoraRiesgo.DESPEJADO),
                contarNivel(materias, CalculadoraRiesgo.NUBLADO),
                contarNivel(materias, CalculadoraRiesgo.TORMENTA),
                (int) materias.stream().filter(m -> EstadoMateriaService.INCOMPLETA.equals(m.estado())).count());
    }

    private int contarNivel(List<MateriaPronostico> materias, String nivel) {
        return (int) materias.stream().filter(m -> nivel.equals(m.nivelRiesgo())).count();
    }
}
