package co.edu.autonoma.barometro.service;

import java.util.Map;

/**
 * Fórmula de riesgo académico (RF-03), según el documento de la fórmula y el
 * Reglamento General Estudiantil de la UAM (Acuerdo 001 de 2026).
 *
 * <p>Art. 37: los cortes pesan 30%, 35% y 35%. Art. 39: la nota de cada corte y la
 * definitiva se redondean a un decimal, y se aprueba con 3.0; por eso la nota
 * necesaria se calcula hacia 2.95, que ya redondea a 3.0.
 */
public final class CalculadoraRiesgo {

    public static final String DESPEJADO = "DESPEJADO";
    public static final String NUBLADO = "NUBLADO";
    public static final String TORMENTA = "TORMENTA";

    private static final Map<Integer, Double> PESO_DE_CADA_CORTE = Map.of(1, 30.0, 2, 35.0, 3, 35.0);
    private static final double NOTA_PARA_APROBAR = 3.0;
    private static final double NOTA_QUE_REDONDEA_A_TRES = 2.95;
    private static final double DESPEJADO_HASTA = 3.5;
    private static final double NUBLADO_HASTA = 4.5;
    private static final double TOLERANCIA = 0.000001;

    private CalculadoraRiesgo() {
    }

    /** Resultado del cálculo. notaNecesaria es null si ya no queda nada por evaluar. */
    public record Resultado(String nivelRiesgo, double notaAcumulada,
                            double porcentajeRestante, Double notaNecesaria) {
    }

    /**
     * @param notaDeCadaCorte nota de cada corte completo, sin redondear (corte → nota)
     */
    public static Resultado calcular(Map<Integer, Double> notaDeCadaCorte) {
        double notaAcumulada = 0;
        double porcentajeEvaluado = 0;
        for (Map.Entry<Integer, Double> corte : notaDeCadaCorte.entrySet()) {
            double peso = PESO_DE_CADA_CORTE.get(corte.getKey());
            notaAcumulada += redondearUnDecimal(corte.getValue()) * peso / 100;
            porcentajeEvaluado += peso;
        }
        double porcentajeRestante = 100 - porcentajeEvaluado;

        if (porcentajeRestante <= TOLERANCIA) {
            double notaFinal = redondearUnDecimal(notaAcumulada);
            String nivel = notaFinal >= NOTA_PARA_APROBAR - TOLERANCIA ? DESPEJADO : TORMENTA;
            return new Resultado(nivel, conDecimales(notaAcumulada, 3), 0, null);
        }

        double notaNecesaria = (NOTA_QUE_REDONDEA_A_TRES - notaAcumulada) / (porcentajeRestante / 100);
        return new Resultado(clasificar(notaNecesaria), conDecimales(notaAcumulada, 3),
                porcentajeRestante, conDecimales(Math.max(notaNecesaria, 0), 2));
    }

    /** Clasifica la nota necesaria con los umbrales 3.5 y 4.5, ambos incluidos. */
    public static String clasificar(double notaNecesaria) {
        if (notaNecesaria <= DESPEJADO_HASTA + TOLERANCIA) {
            return DESPEJADO;
        }
        if (notaNecesaria <= NUBLADO_HASTA + TOLERANCIA) {
            return NUBLADO;
        }
        return TORMENTA;
    }

    /** Art. 39: un decimal, aproximando por exceso o por defecto (2.95 → 3.0, 2.94 → 2.9). */
    public static double redondearUnDecimal(double nota) {
        return Math.round(nota * 10 + TOLERANCIA) / 10.0;
    }

    private static double conDecimales(double valor, int decimales) {
        double factor = Math.pow(10, decimales);
        return Math.round(valor * factor) / factor;
    }
}