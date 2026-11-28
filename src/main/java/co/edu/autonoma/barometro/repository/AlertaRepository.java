package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Alerta;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio en memoria de alertas (RF-05). Sin base de datos en este sprint.
 *
 * <p>No decide cuándo hay alerta: Backend valida que la materia esté completa
 * (RN-01) y en Nublado o Tormenta, y que no exista ya una alerta para esa materia
 * y corte, antes de llamar a {@link #guardar(Alerta)}.</p>
 */
@Repository
public class AlertaRepository {

    /** De la más reciente a la más antigua; a igual fecha, la de id mayor primero. */
    private static final Comparator<Alerta> MAS_RECIENTE_PRIMERO =
            Comparator.comparing(Alerta::getFechaGeneracion,
                            Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(Alerta::getId, Comparator.reverseOrder());

    private final Map<Long, Alerta> alertas = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    /**
     * Guarda la alerta; si no tiene id le asigna uno incremental y, si no tiene
     * fecha de generación, le pone la fecha y hora actual (CA-05.8).
     */
    public Alerta guardar(Alerta alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException("No se puede guardar una alerta vacía.");
        }
        if (alerta.getId() == null) {
            alerta.setId(secuencia.incrementAndGet());
        }
        if (alerta.getFechaGeneracion() == null) {
            alerta.setFechaGeneracion(LocalDateTime.now());
        }
        alertas.put(alerta.getId(), alerta);
        return alerta;
    }

    /** Vacío si la alerta no existe. Sirve para validar la tutoría pedida desde una alerta (RN-02). */
    public Optional<Alerta> buscarPorId(Long id) {
        return id == null ? Optional.empty() : Optional.ofNullable(alertas.get(id));
    }

    /** Alertas de una materia, de la más reciente a la más antigua. */
    public List<Alerta> listarPorMateria(Long idMateria) {
        return alertas.values().stream()
                .filter(a -> Objects.equals(a.getIdMateria(), idMateria))
                .sorted(MAS_RECIENTE_PRIMERO)
                .toList();
    }

    /**
     * Alertas de varias materias, de la más reciente a la más antigua. Backend le pasa
     * los ids de las materias del estudiante para armar GET /api/alertas.
     */
    public List<Alerta> listarPorMaterias(Collection<Long> idsMaterias) {
        if (idsMaterias == null || idsMaterias.isEmpty()) {
            return List.of();
        }
        return alertas.values().stream()
                .filter(a -> idsMaterias.contains(a.getIdMateria()))
                .sorted(MAS_RECIENTE_PRIMERO)
                .toList();
    }

    /** La alerta de una materia en un corte, si existe (como máximo hay una). */
    public Optional<Alerta> buscarPorMateriaYCorte(Long idMateria, Integer corte) {
        return alertas.values().stream()
                .filter(a -> Objects.equals(a.getIdMateria(), idMateria)
                        && Objects.equals(a.getCorte(), corte))
                .findFirst();
    }

    /** true si ya hay alerta para esa materia y ese corte; Backend lo usa para no duplicarla. */
    public boolean existePorMateriaYCorte(Long idMateria, Integer corte) {
        return buscarPorMateriaYCorte(idMateria, corte).isPresent();
    }
}
