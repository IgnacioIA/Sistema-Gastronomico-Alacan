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
class RegistrarIngresoStockApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Long crearIngrediente(String nombre) throws Exception {

        String body = "{\"nombre\":\"" + nombre + "\",\"unidadMedidaId\":1,\"stockMinimo\":5}";

        MvcResult result = mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    @Test
    void registraIngresoYActualizaElStock() throws Exception {

        Long id = crearIngrediente("Nuez moscada");

        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 30}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(30)))
                .andExpect(jsonPath("$.stock.cantidadDisponible", is(30)));

        // Un segundo ingreso se acumula sobre el anterior.
        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(40)));
    }

    @Test
    void rechazaCantidadInvalida() throws Exception {

        Long id = crearIngrediente("Clavo de olor");

        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void rechazaIngresoSobreArticuloDesactivado() throws Exception {

        Long id = crearIngrediente("Ajo en polvo");

        mockMvc.perform(post("/ingredients/" + id + "/desactivar")).andExpect(status().isOk());

        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 10}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("ARTICULO_INACTIVO")));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        mockMvc.perform(post("/ingredients/999999/ingresos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }
}
