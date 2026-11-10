package co.edu.autonoma.barometro.service;

import co.edu.autonoma.barometro.dto.MateriaRequest;
import co.edu.autonoma.barometro.dto.MateriaResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de registro y consulta de materias (RF-01).
 *
 * <p>Pendiente: conectar con la entidad Materia y su repositorio en memoria (SCRUM-37, Datos).
 * Las validaciones de entrada ya se aplican antes de llegar aquí (ver MateriaRequest).
 */
@Service
public class MateriaService {

    public MateriaResponse registrar(MateriaRequest request) {
        throw new UnsupportedOperationException("Pendiente: falta la entidad Materia y su repositorio (SCRUM-37).");
    }

    public List<MateriaResponse> listar() {
        throw new UnsupportedOperationException("Pendiente: falta la entidad Materia y su repositorio (SCRUM-37).");
    }
}
