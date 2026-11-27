package co.edu.autonoma.barometro.model;

/**
 * Nivel de riesgo de una materia (RF-03), según el documento de la fórmula de riesgo.
 *
 * <p>Se clasifica con la nota necesaria para aprobar: Despejado hasta 3.5, Nublado
 * hasta 4.5 y Tormenta por encima de 4.5. Con información incompleta no hay nivel
 * (RN-01).</p>
 */
public enum NivelRiesgo {
    DESPEJADO,
    NUBLADO,
    TORMENTA
}
