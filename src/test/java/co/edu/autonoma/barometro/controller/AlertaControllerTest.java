package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.AlertaResponse;
import co.edu.autonoma.barometro.dto.OpcionTutoria;
import co.edu.autonoma.barometro.service.AlertaService;
import co.edu.autonoma.barometro.service.EstadoMateriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertaController.class)
class AlertaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertaService alertaService;

    @Test
    void listarAlertasResponde200ConElFormatoDelContrato() throws Exception {
        AlertaResponse alerta = new AlertaResponse(1L, 2L, "Álgebra Lineal", 3, "NUBLADO", 3.79,
                LocalDateTime.of(2026, 11, 10, 9, 30), LocalDate.of(2026, 11, 28), 18,
                "Necesitas 3.79 en el corte 3 para aprobar Álgebra Lineal.",
                new OpcionTutoria(true, "/api/tutorias"), EstadoMateriaService.AVISO);
        when(alertaService.listar()).thenReturn(List.of(alerta));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materiaId").value(2))
                .andExpect(jsonPath("$[0].materia").value("Álgebra Lineal"))
                .andExpect(jsonPath("$[0].corte").value(3))
                .andExpect(jsonPath("$[0].nivelRiesgo").value("NUBLADO"))
                .andExpect(jsonPath("$[0].notaNecesaria").value(3.79))
                .andExpect(jsonPath("$[0].fechaGeneracion").value("2026-11-10T09:30:00"))
                .andExpect(jsonPath("$[0].fechaCierreCorte").value("2026-11-28"))
                .andExpect(jsonPath("$[0].diasParaCierre").value(18))
                .andExpect(jsonPath("$[0].tutoria.disponible").value(true))
                .andExpect(jsonPath("$[0].tutoria.solicitarEn").value("/api/tutorias"))
                .andExpect(jsonPath("$[0].aviso").value(EstadoMateriaService.AVISO));
    }

    @Test
    void sinAlertasRespondeListaVacia() throws Exception {
        when(alertaService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
