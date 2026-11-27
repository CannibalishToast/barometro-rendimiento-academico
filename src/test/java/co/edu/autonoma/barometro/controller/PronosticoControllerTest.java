package co.edu.autonoma.barometro.controller;

import co.edu.autonoma.barometro.dto.MateriaPronostico;
import co.edu.autonoma.barometro.dto.PronosticoResponse;
import co.edu.autonoma.barometro.dto.ResumenPronostico;
import co.edu.autonoma.barometro.service.EstadoMateriaService;
import co.edu.autonoma.barometro.service.PronosticoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PronosticoController.class)
class PronosticoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PronosticoService pronosticoService;

    @Test
    void pronosticoResponde200ConElFormatoDelContrato() throws Exception {
        PronosticoResponse pronostico = new PronosticoResponse(List.of(
                new MateriaPronostico(1L, "Cálculo Diferencial", EstadoMateriaService.INCOMPLETA, null, null),
                new MateriaPronostico(2L, "Álgebra Lineal", EstadoMateriaService.COMPLETA, "NUBLADO", 3.79)),
                new ResumenPronostico(0, 1, 0, 1), EstadoMateriaService.AVISO);
        when(pronosticoService.consultar()).thenReturn(pronostico);

        mockMvc.perform(get("/api/pronostico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.materias[0].estado").value("INFORMACION_INCOMPLETA"))
                .andExpect(jsonPath("$.materias[0].nivelRiesgo").value(nullValue()))
                .andExpect(jsonPath("$.materias[1].materia").value("Álgebra Lineal"))
                .andExpect(jsonPath("$.materias[1].nivelRiesgo").value("NUBLADO"))
                .andExpect(jsonPath("$.materias[1].notaNecesaria").value(3.79))
                .andExpect(jsonPath("$.resumen.despejado").value(0))
                .andExpect(jsonPath("$.resumen.nublado").value(1))
                .andExpect(jsonPath("$.resumen.tormenta").value(0))
                .andExpect(jsonPath("$.resumen.incompletas").value(1))
                .andExpect(jsonPath("$.aviso").value(EstadoMateriaService.AVISO));
    }
}
