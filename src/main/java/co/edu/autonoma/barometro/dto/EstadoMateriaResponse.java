package co.edu.autonoma.barometro.dto;

import java.util.List;

/**
 * Estado de la información de una materia y su nivel de riesgo (esquema EstadoMateria
 * del contrato, RF-03 y RF-04).
 *
 * <p>Con información incompleta no se calcula riesgo ni se genera alerta, por eso los
 * campos del riesgo van en null (RN-01, RNF-03). El aviso de RN-03 va en toda respuesta.
 */
public record EstadoMateriaResponse(
        Long materiaId,
        String estado,
        String nivelRiesgo,
        Double notaAcumulada,
        Double porcentajeRestante,
        Double notaNecesaria,
        Boolean alertaGenerada,
        List<CorteFaltante> faltantes,
        String mensaje,
        String aviso
) {
}