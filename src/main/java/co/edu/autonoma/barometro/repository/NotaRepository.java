package co.edu.autonoma.barometro.repository;

import co.edu.autonoma.barometro.model.Nota;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repositorio en memoria de notas (RF-02). Sin base de datos en este sprint.
 *
 * <p>No valida reglas de negocio: Backend valida antes de guardar y, si rechaza
 * la nota, no llama a {@link #guardar(Nota)}, así no se guarda nada (CA-02.3).</p>
 */
@Repository
public class NotaRepository {

    private final Map<Long, Nota> notas = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    /**
     * Guarda la nota; si no tiene id le asigna uno incremental y, si no tiene
     * fecha de registro, le pone la fecha y hora actual.
     */
    public Nota guardar(Nota nota) {
        if (nota == null) {
            throw new IllegalArgumentException("No se puede guardar una nota vacía.");
        }
        if (nota.getId() == null) {
            nota.setId(secuencia.incrementAndGet());
        }
        if (nota.getFechaRegistro() == null) {
            nota.setFechaRegistro(LocalDateTime.now());
        }
        notas.put(nota.getId(), nota);
        return nota;
    }

    /** Notas de una materia, en orden de id. */
    public List<Nota> listarPorMateria(Long idMateria) {
        return notas.values().stream()
                .filter(n -> Objects.equals(n.getIdMateria(), idMateria))
                .sorted(Comparator.comparing(Nota::getId))
                .toList();
    }

    /** Notas de una materia agrupadas por corte, con los cortes en orden (CA-02.5). */
    public Map<Integer, List<Nota>> listarPorMateriaAgrupadasPorCorte(Long idMateria) {
        return listarPorMateria(idMateria).stream()
                .filter(n -> n.getCorte() != null)
                .collect(Collectors.groupingBy(Nota::getCorte, TreeMap::new, Collectors.toList()));
    }

    /**
     * Porcentaje acumulado de una materia en un corte. Backend lo usa para no
     * dejar pasar del 100% (CA-02.2). Devuelve 0.0 si no hay notas.
     */
    public double sumaPorcentajes(Long idMateria, Integer corte) {
        return listarPorMateria(idMateria).stream()
                .filter(n -> Objects.equals(n.getCorte(), corte))
                .map(Nota::getPorcentaje)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
