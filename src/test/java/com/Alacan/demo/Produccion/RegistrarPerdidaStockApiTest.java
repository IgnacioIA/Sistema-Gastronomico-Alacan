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
class RegistrarPerdidaStockApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Long crearIngredienteConStock(String nombre, int cantidadInicial) throws Exception {

        String stockInicial = cantidadInicial > 0 ? ",\"stockInicial\":{\"cantidad\":" + cantidadInicial + "}" : "";
        String body = "{\"nombre\":\"" + nombre + "\",\"unidadMedidaId\":1,\"stockMinimo\":5" + stockInicial + "}";

        MvcResult result = mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    @Test
    void registraPerdidaYDescuentaElStock() throws Exception {

        Long id = crearIngredienteConStock("Tomate", 50);

        mockMvc.perform(post("/ingredients/" + id + "/perdidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock.cantidadActual", is(30)))
                .andExpect(jsonPath("$.stock.cantidadDisponible", is(30)));
    }

    @Test
    void rechazaPerdidaMayorAlStockDisponible() throws Exception {

        Long id = crearIngredienteConStock("Lechuga", 10);

        mockMvc.perform(post("/ingredients/" + id + "/perdidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 50}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void rechazaCantidadInvalida() throws Exception {

        Long id = crearIngredienteConStock("Zanahoria", 10);

        mockMvc.perform(post("/ingredients/" + id + "/perdidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void rechazaPerdidaSobreArticuloDesactivado() throws Exception {

        Long id = crearIngredienteConStock("Papa", 10);

        mockMvc.perform(post("/ingredients/" + id + "/desactivar")).andExpect(status().isOk());

        mockMvc.perform(post("/ingredients/" + id + "/perdidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 5}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("ARTICULO_INACTIVO")));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        mockMvc.perform(post("/ingredients/999999/perdidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 5}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }
}
