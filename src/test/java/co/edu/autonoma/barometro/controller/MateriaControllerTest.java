package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.HorarioDto;
import co.edu.autonoma.barometro.dto.MateriaRequest;
import co.edu.autonoma.barometro.dto.MateriaResponse;
import co.edu.autonoma.barometro.service.MateriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MateriaController.class)
class MateriaControllerTest {

    private static final String MATERIA_VALIDA = """
            {
              "nombre": "Cálculo Integral",
              "docente": "Laura Gómez",
              "horario": { "dias": ["LUNES", "MIERCOLES"], "horaInicio": "08:00", "horaFin": "10:00" },
              "salon": "B-204"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MateriaService materiaService;

    private MateriaResponse materiaRegistrada() {
        HorarioDto horario = new HorarioDto(List.of("LUNES", "MIERCOLES"), LocalTime.of(8, 0), LocalTime.of(10, 0));
        return new MateriaResponse(1L, "Cálculo Integral", "Laura Gómez", horario, "B-204");
    }

    @Test
    void registrarMateriaValidaResponde201() throws Exception {
        when(materiaService.registrar(any(MateriaRequest.class))).thenReturn(materiaRegistrada());

        mockMvc.perform(post("/api/materias").contentType(MediaType.APPLICATION_JSON).content(MATERIA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cálculo Integral"))
                .andExpect(jsonPath("$.salon").value("B-204"));
    }

    @Test
    void registrarMateriaSinSalonResponde400YNoGuarda() throws Exception {
        String sinSalon = MATERIA_VALIDA.replace("\"salon\": \"B-204\"", "\"salon\": \"\"");

        mockMvc.perform(post("/api/materias").contentType(MediaType.APPLICATION_JSON).content(sinSalon))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Falta el salón de la materia."));

        verify(materiaService, never()).registrar(any());
    }

    @Test
    void registrarMateriaSinHorarioResponde400() throws Exception {
        String sinHorario = """
                { "nombre": "Cálculo Integral", "docente": "Laura Gómez", "salon": "B-204" }
                """;

        mockMvc.perform(post("/api/materias").contentType(MediaType.APPLICATION_JSON).content(sinHorario))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Falta el horario de la materia."));

        verify(materiaService, never()).registrar(any());
    }

    @Test
    void listarMateriasResponde200() throws Exception {
        when(materiaService.listar()).thenReturn(List.of(materiaRegistrada()));

        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Cálculo Integral"));
    }
}
