package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class RegistrarCorreccionStockApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Long crearIngredienteConStock(String nombre, int cantidadInicial) throws Exception {

        String body = "{\"nombre\":\"" + nombre + "\",\"unidadMedidaId\":1,\"stockMinimo\":5,"
                + "\"stockInicial\":{\"cantidad\":" + cantidadInicial + "}}";

        MvcResult result = mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    @Test
    void correccionHaciaArribaRegistraIngreso() throws Exception {

        Long id = crearIngredienteConStock("Arroz", 20);

        mockMvc.perform(post("/ingredients/" + id + "/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": 35}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(35)));
    }

    @Test
    void correccionHaciaAbajoRegistraSalida() throws Exception {

        Long id = crearIngredienteConStock("Fideos", 20);

        mockMvc.perform(post("/ingredients/" + id + "/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": 12}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(12)));
    }

    @Test
    void correccionIgualAlActualNoGeneraCambios() throws Exception {

        Long id = crearIngredienteConStock("Polenta", 20);

        mockMvc.perform(post("/ingredients/" + id + "/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": 20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(20)));
    }

    @Test
    void rechazaCantidadRealNegativa() throws Exception {

        Long id = crearIngredienteConStock("Avena", 20);

        mockMvc.perform(post("/ingredients/" + id + "/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": -5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void rechazaCorreccionSobreArticuloDesactivado() throws Exception {

        Long id = crearIngredienteConStock("Lenteja", 20);

        mockMvc.perform(post("/ingredients/" + id + "/desactivar")).andExpect(status().isOk());

        mockMvc.perform(post("/ingredients/" + id + "/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": 10}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("ARTICULO_INACTIVO")));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        mockMvc.perform(post("/ingredients/999999/correcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidadReal\": 10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }
}
