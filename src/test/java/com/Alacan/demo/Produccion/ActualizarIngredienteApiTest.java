package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class ActualizarIngredienteApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Long crearIngrediente(String nombre, long unidadMedidaId, boolean conStockInicial) throws Exception {

        String stockInicial = conStockInicial ? ",\"stockInicial\":{\"cantidad\":10}" : "";
        String body = "{\"nombre\":\"" + nombre + "\",\"unidadMedidaId\":" + unidadMedidaId
                + ",\"stockMinimo\":5" + stockInicial + "}";

        MvcResult result = mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    @Test
    void actualizaTodosLosCamposCuandoNoHayHistorial() throws Exception {

        Long id = crearIngrediente("Harina vieja", 2, false);

        String body = """
                {
                  "nombre": "Harina 000 corregida",
                  "descripcion": "Ahora con descripcion",
                  "unidadMedidaId": 1,
                  "stockMinimo": 15
                }
                """;

        mockMvc.perform(put("/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Harina 000 corregida")))
                .andExpect(jsonPath("$.descripcion", is("Ahora con descripcion")))
                .andExpect(jsonPath("$.unidadMedida.nombre", is("GRAMO")))
                .andExpect(jsonPath("$.stock.stockMinimo", is(15)));
    }

    @Test
    void bloqueaCambioDeUnidadCuandoYaHayStockFisico() throws Exception {

        Long id = crearIngrediente("Sal con stock", 1, true);

        String body = """
                {
                  "nombre": "Sal con stock",
                  "unidadMedidaId": 2,
                  "stockMinimo": 5
                }
                """;

        mockMvc.perform(put("/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("UNIDAD_MEDIDA_BLOQUEADA")));
    }

    @Test
    void permiteActualizarOtrosCamposAunqueNoSeToqueLaUnidadConHistorial() throws Exception {

        Long id = crearIngrediente("Azucar con stock", 1, true);

        String body = """
                {
                  "nombre": "Azucar impalpable",
                  "unidadMedidaId": 1,
                  "stockMinimo": 50
                }
                """;

        mockMvc.perform(put("/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Azucar impalpable")))
                .andExpect(jsonPath("$.stock.stockMinimo", is(50)));
    }

    @Test
    void nombreVacioDevuelve400() throws Exception {

        Long id = crearIngrediente("Cacao", 1, false);

        String body = """
                {
                  "nombre": "",
                  "unidadMedidaId": 1,
                  "stockMinimo": 5
                }
                """;

        mockMvc.perform(put("/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        String body = """
                {
                  "nombre": "No existe",
                  "unidadMedidaId": 1,
                  "stockMinimo": 5
                }
                """;

        mockMvc.perform(put("/ingredients/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }

    @Test
    void unidadMedidaInexistenteDevuelve404() throws Exception {

        Long id = crearIngrediente("Vainilla", 1, false);

        String body = """
                {
                  "nombre": "Vainilla",
                  "unidadMedidaId": 9999,
                  "stockMinimo": 5
                }
                """;

        mockMvc.perform(put("/ingredients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("UNIDAD_MEDIDA_NOT_FOUND")));
    }
}
