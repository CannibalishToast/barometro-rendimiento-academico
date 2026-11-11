package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Materia;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio en memoria de materias (RF-01). Sin base de datos en este sprint.
 *
 * <p>No valida reglas de negocio: Backend valida en el servicio y este
 * repositorio guarda lo que recibe.</p>
 */
@Repository
public class MateriaRepository {

    private final Map<Long, Materia> materias = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    /** Guarda la materia; si no tiene id le asigna uno incremental (CA-01.1). */
    public Materia guardar(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("No se puede guardar una materia vacía.");
        }
        if (materia.getId() == null) {
            materia.setId(secuencia.incrementAndGet());
        }
        materias.put(materia.getId(), materia);
        return materia;
    }

    /** Materias de un estudiante, en orden de id (CA-01.3). */
    public List<Materia> listarPorEstudiante(Long idEstudiante) {
        return materias.values().stream()
                .filter(m -> Objects.equals(m.getIdEstudiante(), idEstudiante))
                .sorted(Comparator.comparing(Materia::getId))
                .toList();
    }

    /** Vacío si la materia no existe (Backend responde 404). */
    public Optional<Materia> buscarPorId(Long id) {
        return id == null ? Optional.empty() : Optional.ofNullable(materias.get(id));
    }
}
