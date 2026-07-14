package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class CrearIngredienteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void creaIngredienteConStockInicialYDevuelve201() throws Exception {

        String body = """
                {
                  "nombre": "Harina 000",
                  "unidadMedidaId": 2,
                  "descripcion": "Harina de trigo",
                  "stockMinimo": 5,
                  "stockInicial": { "cantidad": 25 }
                }
                """;

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre", is("Harina 000")))
                .andExpect(jsonPath("$.tipoArticulo.nombre", is("INGREDIENTE")))
                .andExpect(jsonPath("$.unidadMedida.nombre", is("KILOGRAMO")))
                .andExpect(jsonPath("$.unidadMedida.abreviatura", is("kg")))
                .andExpect(jsonPath("$.stock.cantidadActual", is(25)))
                .andExpect(jsonPath("$.stock.stockMinimo", is(5)))
                .andExpect(jsonPath("$.stock.necesitaReposicion", is(false)));
    }

    @Test
    void creaIngredienteSinStockInicialQuedaEnCero() throws Exception {

        String body = """
                {
                  "nombre": "Sal fina",
                  "unidadMedidaId": 1
                }
                """;

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock.cantidadActual", is(0)))
                .andExpect(jsonPath("$.stock.stockMinimo", is(0)))
                .andExpect(jsonPath("$.stock.necesitaReposicion", is(false)));
    }

    @Test
    void nombreVacioDevuelve400ConCodigoEstable() throws Exception {

        String body = """
                {
                  "nombre": "",
                  "unidadMedidaId": 1
                }
                """;

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_ERROR")));
    }

    @Test
    void unidadMedidaInexistenteDevuelve404ConCodigoEstable() throws Exception {

        String body = """
                {
                  "nombre": "Azucar",
                  "unidadMedidaId": 9999
                }
                """;

        mockMvc.perform(post("/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("UNIDAD_MEDIDA_NOT_FOUND")));
    }
}
