package co.edu.autonoma.barometro;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de punta a punta de RF-05 con la aplicación completa: solo usa los endpoints,
 * igual que lo haría el frontend. Registra notas con POST y revisa que la alerta, el
 * estado de la materia y el pronóstico cuenten la misma historia.
 *
 * <p>El reloj está fijo en el 10 de noviembre de 2026, así el corte 3 (cierra el 28 de
 * noviembre) está a 18 días sin importar cuándo se corra la prueba. Cada prueba arranca
 * con la aplicación limpia ({@link DirtiesContext}) y sin los datos de ejemplo de
 * DatosDePrueba, para que solo cuenten las materias que crea la prueba.
 */
@SpringBootTest(properties = "barometro.datos-prueba.habilitados=false")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AlertasFlujoCompletoTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    @TestConfiguration
    static class RelojDePrueba {
        @Bean
        @Primary
        Clock relojFijo() {
            return Clock.fixed(LocalDateTime.of(2026, 11, 10, 9, 30).atZone(ZONA).toInstant(), ZONA);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    // Crea una materia con POST /api/materias y devuelve su id
    private long crearMateria(String nombre) throws Exception {
        String json = """
                {"nombre": "%s", "docente": "Laura Gómez",
                 "horario": {"dias": ["LUNES"], "horaInicio": "08:00", "horaFin": "10:00"},
                 "salon": "B-204"}
                """.formatted(nombre);
        String respuesta = mockMvc.perform(post("/api/materias")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(respuesta, "$.id")).longValue();
    }

    // Registra una nota con POST /api/materias/{id}/notas
    private void registrarNota(long materiaId, int corte, double valor, double porcentaje) throws Exception {
        String json = """
                {"corte": %d, "descripcion": "Parcial", "valor": %s, "porcentaje": %s}
                """.formatted(corte, valor, porcentaje);
        mockMvc.perform(post("/api/materias/" + materiaId + "/notas")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertaGenerada").doesNotExist());
    }

    @Test
    void alQuedarEnNubladoSaleLaAlertaYTodoCoincide() throws Exception {
        long materiaId = crearMateria("Álgebra Lineal");

        registrarNota(materiaId, 1, 2.5, 100);
        mockMvc.perform(get("/api/alertas")).andExpect(jsonPath("$", empty()));

        registrarNota(materiaId, 2, 2.5, 100);

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].materiaId").value(materiaId))
                .andExpect(jsonPath("$[0].materia").value("Álgebra Lineal"))
                .andExpect(jsonPath("$[0].corte").value(3))
                .andExpect(jsonPath("$[0].nivelRiesgo").value("NUBLADO"))
                .andExpect(jsonPath("$[0].notaNecesaria").value(3.79))
                .andExpect(jsonPath("$[0].fechaGeneracion").value("2026-11-10T09:30:00"))
                .andExpect(jsonPath("$[0].fechaCierreCorte").value("2026-11-28"))
                .andExpect(jsonPath("$[0].diasParaCierre").value(18))
                .andExpect(jsonPath("$[0].mensaje")
                        .value("Necesitas 3.79 en el corte 3 para aprobar Álgebra Lineal."))
                .andExpect(jsonPath("$[0].tutoria.disponible").value(true))
                .andExpect(jsonPath("$[0].tutoria.solicitarEn").value("/api/tutorias"))
                .andExpect(jsonPath("$[0].aviso").exists());

        mockMvc.perform(get("/api/materias/" + materiaId + "/estado"))
                .andExpect(jsonPath("$.nivelRiesgo").value("NUBLADO"))
                .andExpect(jsonPath("$.alertaGenerada").value(true));

        mockMvc.perform(get("/api/pronostico"))
                .andExpect(jsonPath("$.materias[?(@.materiaId == " + materiaId + ")].nivelRiesgo",
                        contains("NUBLADO")))
                .andExpect(jsonPath("$.resumen.nublado").value(1));
    }

    @Test
    void salUnaAlertaPorCorteSinDuplicar() throws Exception {
        long materiaId = crearMateria("Física");

        // Con 1.0 en el corte 1 ya necesita 3.79: Nublado, alerta para el corte 2
        registrarNota(materiaId, 1, 1.0, 100);
        // El corte 2 a medias deja la materia incompleta: no pasa nada (RN-01)
        registrarNota(materiaId, 2, 1.0, 50);
        // Al completar el corte 2 necesita 6.43: Tormenta, alerta para el corte 3
        registrarNota(materiaId, 2, 1.0, 50);
        // Otra nota del corte 2 no cabe; una del corte 3 la deja incompleta y no duplica
        registrarNota(materiaId, 3, 1.0, 40);

        mockMvc.perform(get("/api/alertas"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].corte").value(3))
                .andExpect(jsonPath("$[0].nivelRiesgo").value("TORMENTA"))
                .andExpect(jsonPath("$[1].corte").value(2))
                .andExpect(jsonPath("$[1].nivelRiesgo").value("NUBLADO"));
    }

    @Test
    void conInformacionIncompletaNoHayAlertaNiNivel() throws Exception {
        long materiaId = crearMateria("Química");

        registrarNota(materiaId, 1, 1.0, 60);

        mockMvc.perform(get("/api/alertas")).andExpect(jsonPath("$", empty()));
        mockMvc.perform(get("/api/materias/" + materiaId + "/estado"))
                .andExpect(jsonPath("$.estado").value("INFORMACION_INCOMPLETA"))
                .andExpect(jsonPath("$.alertaGenerada").value(false));
        mockMvc.perform(get("/api/pronostico"))
                .andExpect(jsonPath("$.materias[?(@.materiaId == " + materiaId + ")].estado",
                        contains("INFORMACION_INCOMPLETA")));
    }

    @Test
    void conDespejadoNoHayAlerta() throws Exception {
        long materiaId = crearMateria("Cálculo");

        registrarNota(materiaId, 1, 3.0, 100);
        registrarNota(materiaId, 2, 2.5, 100);

        mockMvc.perform(get("/api/alertas")).andExpect(jsonPath("$", empty()));
        mockMvc.perform(get("/api/materias/" + materiaId + "/estado"))
                .andExpect(jsonPath("$.nivelRiesgo").value("DESPEJADO"))
                .andExpect(jsonPath("$.alertaGenerada").value(false));
    }
}
