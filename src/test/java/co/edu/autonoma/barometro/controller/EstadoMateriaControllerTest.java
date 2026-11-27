package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.CorteFaltante;
import co.edu.autonoma.barometro.dto.EstadoMateriaResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.service.CalculadoraRiesgo;
import co.edu.autonoma.barometro.service.EstadoMateriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EstadoMateriaController.class)
class EstadoMateriaControllerTest {

    private static final String URL = "/api/materias/2/estado";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstadoMateriaService estadoMateriaService;

    @Test
    void estadoIncompletoResponde200ConLoQueFalta() throws Exception {
        EstadoMateriaResponse incompleta = new EstadoMateriaResponse(2L,
        // los 4 null son nivelRiesgo, notaAcumulada, porcentajeRestante y notaNecesaria respectivamente, tienen el mismo orden del record
        // Y son nulos porque esta es una prueba de materia incompleta, por lo que no se calcula el riesgo ni se genera alerta
                EstadoMateriaService.INCOMPLETA, null, null, null, null,
                false, List.of(new CorteFaltante(2, 60.0)),
                "Faltan notas del corte 2 (llevas 60% de 100%). Completa tus notas para calcular el estado.",
                EstadoMateriaService.AVISO);
        when(estadoMateriaService.consultar(2L)).thenReturn(incompleta);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INFORMACION_INCOMPLETA"))
                .andExpect(jsonPath("$.alertaGenerada").value(false))
                .andExpect(jsonPath("$.faltantes[0].corte").value(2))
                .andExpect(jsonPath("$.faltantes[0].porcentajeRegistrado").value(60.0))
                .andExpect(jsonPath("$.aviso")
                        .value("Este sistema no es la fuente oficial de notas. Verifica en intraUAM."));
    }
    
    @Test
    void estadoCompletoResponde200ConElNivelDeRiesgo() throws Exception {
        EstadoMateriaResponse completa = new EstadoMateriaResponse(2L,
                EstadoMateriaService.COMPLETA, CalculadoraRiesgo.DESPEJADO, 1.775, 35.0, 3.36,
                false, List.of(), "La información de la materia está completa.",
                EstadoMateriaService.AVISO);
        when(estadoMateriaService.consultar(2L)).thenReturn(completa);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETA"))
                .andExpect(jsonPath("$.nivelRiesgo").value("DESPEJADO"))
                .andExpect(jsonPath("$.notaAcumulada").value(1.775))
                .andExpect(jsonPath("$.porcentajeRestante").value(35.0))
                .andExpect(jsonPath("$.notaNecesaria").value(3.36))
                .andExpect(jsonPath("$.alertaGenerada").value(false))
                .andExpect(jsonPath("$.faltantes").isEmpty());
    }

    @Test
    void materiaQueNoExisteResponde404() throws Exception {
        when(estadoMateriaService.consultar(2L))
                .thenThrow(new RecursoNoEncontradoException("La materia no existe."));

        mockMvc.perform(get(URL))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("La materia no existe."));
    }
}
