package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.NotaRequest;
import co.edu.autonoma.barometro.dto.NotaResponse;
import co.edu.autonoma.barometro.dto.NotasPorCorteResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.exception.ReglaDeNegocioException;
import co.edu.autonoma.barometro.model.Nota;
import co.edu.autonoma.barometro.repository.MateriaRepository;
import co.edu.autonoma.barometro.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de las notas (RF-02): revisa que la materia exista y que el corte
 * no supere el 100% antes de guardar, y consulta las notas agrupadas por corte.
 */
@Service
public class NotaService {

    private static final double PORCENTAJE_MAXIMO_CORTE = 100.0;
    private static final double TOLERANCIA = 0.000001;

    private final MateriaRepository materiaRepository;
    private final NotaRepository notaRepository;

    public NotaService(MateriaRepository materiaRepository, NotaRepository notaRepository) {
        this.materiaRepository = materiaRepository;
        this.notaRepository = notaRepository;
    }

    public NotaResponse registrar(Long materiaId, NotaRequest request) {
        verificarQueExisteLaMateria(materiaId);

        double yaRegistrado = notaRepository.sumaPorcentajes(materiaId, request.corte());
        if (yaRegistrado + request.porcentaje() > PORCENTAJE_MAXIMO_CORTE + TOLERANCIA) {
            throw new ReglaDeNegocioException(
                    "Los porcentajes del corte no pueden superar el 100%. No se guardó.");
        }

        Nota nota = new Nota(null, materiaId, request.corte(), request.descripcion(),
                request.valor(), request.porcentaje(), null);
        Nota guardada = notaRepository.guardar(nota);
        return aRespuesta(guardada);
    }

    public List<NotasPorCorteResponse> listarPorCorte(Long materiaId) {
        verificarQueExisteLaMateria(materiaId);

        return notaRepository.listarPorMateriaAgrupadasPorCorte(materiaId).entrySet().stream()
                .map(grupo -> aCorte(materiaId, grupo.getKey(), grupo.getValue()))
                .toList();
    }

    private void verificarQueExisteLaMateria(Long materiaId) {
        materiaRepository.buscarPorId(materiaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La materia no existe."));
    }

    private NotaResponse aRespuesta(Nota nota) {
        return new NotaResponse(nota.getId(), nota.getIdMateria(), nota.getCorte(),
                nota.getDescripcion(), nota.getValor(), nota.getPorcentaje());
    }

    private NotasPorCorteResponse aCorte(Long materiaId, Integer corte, List<Nota> notas) {
        double porcentajeRegistrado = notaRepository.sumaPorcentajes(materiaId, corte);
        List<NotaResponse> notasDelCorte = notas.stream().map(this::aRespuesta).toList();
        return new NotasPorCorteResponse(corte, porcentajeRegistrado, notasDelCorte);
    }
}
