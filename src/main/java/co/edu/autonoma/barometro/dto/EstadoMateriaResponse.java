package co.edu.autonoma.barometro.dto;

import java.util.List;

/**
 * Estado de la información de una materia (esquema EstadoMateria del contrato, RF-04).
 *
 * <p>Mientras falten notas no se calcula riesgo ni se genera alerta, por eso
 * alertaGenerada es false (RN-01, RNF-03). El aviso de RN-03 va en toda respuesta.
 */
public record EstadoMateriaResponse(
        Long materiaId,
        String estado,
        Boolean alertaGenerada,
        List<CorteFaltante> faltantes,
        String mensaje,
        String aviso
) {
}
