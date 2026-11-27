package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.NotaRequest;
import co.edu.autonoma.barometro.dto.NotaResponse;
import co.edu.autonoma.barometro.dto.NotasPorCorteResponse;
import co.edu.autonoma.barometro.exception.RecursoNoEncontradoException;
import co.edu.autonoma.barometro.exception.ReglaDeNegocioException;
import co.edu.autonoma.barometro.exception.SinPermisoException;
import co.edu.autonoma.barometro.service.NotaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotaController.class)
class NotaControllerTest {

    private static final String URL = "/api/materias/3/notas";

    private static final String NOTA_VALIDA = """
            { "corte": 1, "descripcion": "Parcial 1", "valor": 3.8, "porcentaje": 30 }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaService notaService;

    private NotaResponse notaRegistrada() {
        return new NotaResponse(1L, 3L, 1, "Parcial 1", 3.8, 30.0);
    }

    @Test
    void registrarNotaValidaResponde201() throws Exception {
        when(notaService.registrar(eq(3L), any(NotaRequest.class))).thenReturn(notaRegistrada());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(NOTA_VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.materiaId").value(3))
                .andExpect(jsonPath("$.valor").value(3.8));
    }

    @Test
    void registrarNotaFueraDeRangoResponde400YNoGuarda() throws Exception {
        String notaDeSiete = NOTA_VALIDA.replace("\"valor\": 3.8", "\"valor\": 7.0");

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(notaDeSiete))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La nota debe estar entre 0.0 y 5.0. No se guardó."));

        verify(notaService, never()).registrar(any(), any());
    }

    @Test
    void registrarNotaSinPorcentajeResponde400YNoGuarda() throws Exception {
        String sinPorcentaje = """
                { "corte": 1, "descripcion": "Parcial 1", "valor": 3.8 }
                """;

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(sinPorcentaje))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Falta el porcentaje de la nota."));

        verify(notaService, never()).registrar(any(), any());
    }

    @Test
    void registrarQueSuperaElCienPorCientoResponde400() throws Exception {
        when(notaService.registrar(eq(3L), any(NotaRequest.class))).thenThrow(new ReglaDeNegocioException(
                "Los porcentajes del corte no pueden superar el 100%. No se guardó."));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(NOTA_VALIDA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Los porcentajes del corte no pueden superar el 100%. No se guardó."));
    }

    @Test
    void registrarEnMateriaQueNoExisteResponde404() throws Exception {
        when(notaService.registrar(eq(3L), any(NotaRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("La materia no existe."));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(NOTA_VALIDA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("La materia no existe."));
    }

    @Test
    void registrarEnMateriaAjenaResponde403() throws Exception {
        when(notaService.registrar(eq(3L), any(NotaRequest.class))).thenThrow(new SinPermisoException(
                "Solo el estudiante dueño de la materia puede registrar notas."));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(NOTA_VALIDA))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Solo el estudiante dueño de la materia puede registrar notas."));
    }

    @Test
    void listarPorCorteResponde200ConLosCortes() throws Exception {
        NotasPorCorteResponse corte1 = new NotasPorCorteResponse(1, 30.0, List.of(notaRegistrada()));
        when(notaService.listarPorCorte(3L)).thenReturn(List.of(corte1));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].corte").value(1))
                .andExpect(jsonPath("$[0].porcentajeRegistrado").value(30.0))
                .andExpect(jsonPath("$[0].notas[0].descripcion").value("Parcial 1"));
    }
}
