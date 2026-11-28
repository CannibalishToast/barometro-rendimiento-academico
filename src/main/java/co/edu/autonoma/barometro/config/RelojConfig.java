package co.edu.autonoma.barometro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Reloj del sistema como bean. Las alertas lo usan para la fecha de generación y los
 * días para el cierre del corte (RF-05); en las pruebas se reemplaza por un reloj fijo
 * para que los resultados no dependan del día en que se corren.
 */
@Configuration
public class RelojConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }
}
