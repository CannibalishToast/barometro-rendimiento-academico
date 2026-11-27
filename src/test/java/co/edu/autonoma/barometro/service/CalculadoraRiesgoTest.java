package co.edu.autonoma.barometro.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CalculadoraRiesgoTest {

    private static final double DELTA = 0.000001;

    @Test
    void ejemploADosCortesCon3y2punto5EsDespejado() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 3.0, 2, 2.5));

        assertEquals(1.775, r.notaAcumulada(), DELTA);
        assertEquals(35.0, r.porcentajeRestante(), DELTA);
        assertEquals(3.36, r.notaNecesaria(), DELTA);
        assertEquals(CalculadoraRiesgo.DESPEJADO, r.nivelRiesgo());
    }

    @Test
    void ejemploBDosCortesEn2punto5EsNublado() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 2.5, 2, 2.5));

        assertEquals(1.625, r.notaAcumulada(), DELTA);
        assertEquals(3.79, r.notaNecesaria(), DELTA);
        assertEquals(CalculadoraRiesgo.NUBLADO, r.nivelRiesgo());
    }

    @Test
    void notasMuyBajasDanTormenta() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 1.0, 2, 1.0));

        assertEquals(6.57, r.notaNecesaria(), DELTA);
        assertEquals(CalculadoraRiesgo.TORMENTA, r.nivelRiesgo());
    }

    @Test
    void soloElPrimerCorteDejaSetentaPorCientoRestante() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 3.0));

        assertEquals(0.9, r.notaAcumulada(), DELTA);
        assertEquals(70.0, r.porcentajeRestante(), DELTA);
        assertEquals(2.93, r.notaNecesaria(), DELTA);
        assertEquals(CalculadoraRiesgo.DESPEJADO, r.nivelRiesgo());
    }

    @Test
    void siYaAseguroLaMateriaLaNotaNecesariaEsCero() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 5.0, 2, 5.0));

        assertEquals(0.0, r.notaNecesaria(), DELTA);
        assertEquals(CalculadoraRiesgo.DESPEJADO, r.nivelRiesgo());
    }

    @Test
    void tresCortesEn3SinErrorDeDecimalesEsDespejado() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 3.0, 2, 3.0, 3, 3.0));

        assertEquals(0.0, r.porcentajeRestante(), DELTA);
        assertNull(r.notaNecesaria());
        assertEquals(CalculadoraRiesgo.DESPEJADO, r.nivelRiesgo());
    }

    @Test
    void ca10NotaFinalDe2punto95ApruebaPorRedondeo() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 4.0, 2, 2.5, 3, 2.5));

        assertEquals(2.95, r.notaAcumulada(), DELTA);
        assertEquals(CalculadoraRiesgo.DESPEJADO, r.nivelRiesgo());
    }

    @Test
    void notaFinalMenorA2punto95EsTormenta() {
        CalculadoraRiesgo.Resultado r = CalculadoraRiesgo.calcular(Map.of(1, 2.0, 2, 2.0, 3, 2.0));

        assertEquals(CalculadoraRiesgo.TORMENTA, r.nivelRiesgo());
    }

    @Test
    void losUmbralesIncluyenEl3punto5yEl4punto5() {
        assertEquals(CalculadoraRiesgo.DESPEJADO, CalculadoraRiesgo.clasificar(3.5));
        assertEquals(CalculadoraRiesgo.DESPEJADO, CalculadoraRiesgo.clasificar(3.5000000000000004));
        assertEquals(CalculadoraRiesgo.NUBLADO, CalculadoraRiesgo.clasificar(3.51));
        assertEquals(CalculadoraRiesgo.NUBLADO, CalculadoraRiesgo.clasificar(4.5));
        assertEquals(CalculadoraRiesgo.TORMENTA, CalculadoraRiesgo.clasificar(4.51));
    }

    @Test
    void redondeaAUnDecimalComoElReglamento() {
        assertEquals(3.0, CalculadoraRiesgo.redondearUnDecimal(2.95), DELTA);
        assertEquals(3.0, CalculadoraRiesgo.redondearUnDecimal(2.96), DELTA);
        assertEquals(2.9, CalculadoraRiesgo.redondearUnDecimal(2.94), DELTA);
    }
}