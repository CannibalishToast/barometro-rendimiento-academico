package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.HorarioDto;
import co.edu.autonoma.barometro.dto.MateriaRequest;
import co.edu.autonoma.barometro.dto.MateriaResponse;
import co.edu.autonoma.barometro.model.Materia;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de registro y consulta de materias (RF-01).
 *
 * <p>Las validaciones de entrada ya se aplican antes de llegar aquí (ver MateriaRequest).
 */
@Service
public class MateriaService {

    // Mientras no haya login, todas las materias son del estudiante de prueba
    private static final Long ESTUDIANTE_PRUEBA_ID = 1L;

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public MateriaResponse registrar(MateriaRequest request) {
        HorarioDto horario = request.horario();
        Materia materia = new Materia(
                null,                    // el id lo asigna el repositorio al guardar
                ESTUDIANTE_PRUEBA_ID,
                request.nombre(),
                request.docente(),
                horario.dias(),
                horario.horaInicio(),
                horario.horaFin(),
                request.salon());

        Materia guardada = materiaRepository.guardar(materia);
        return aRespuesta(guardada);
    }

    public List<MateriaResponse> listar() {
        return materiaRepository
                .listarPorEstudiante(ESTUDIANTE_PRUEBA_ID)
                .stream()
                .map(this::aRespuesta)
                .toList();
    }

    // Convierte la entidad (cómo se guarda) en lo que ve el cliente, sin datos internos
    private MateriaResponse aRespuesta(Materia materia) {
        HorarioDto horario = new HorarioDto(
                materia.getDias(),
                materia.getHoraInicio(),
                materia.getHoraFin());
        return new MateriaResponse(
                materia.getId(),
                materia.getNombre(),
                materia.getDocente(),
                horario,
                materia.getSalon());
    }
}
