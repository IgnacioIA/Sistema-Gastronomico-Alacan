package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class ListarMovimientosApiTest {

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
    void listaLosMovimientosGeneradosPorVariasOperaciones() throws Exception {

        Long id = crearIngredienteConStock("Comino en grano", 20); // genera ALTA

        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\": 10}")); // genera COMPRA

        mockMvc.perform(post("/ingredients/" + id + "/perdidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\": 5}")); // genera DESPERDICIO

        mockMvc.perform(get("/ingredients/" + id + "/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.content.length()", is(3)))
                // El mas reciente primero: la ultima operacion fue la perdida (DESPERDICIO).
                .andExpect(jsonPath("$.content[0].tipoMovimiento.nombre", is("DESPERDICIO")))
                .andExpect(jsonPath("$.content[0].sentido", is("SALIDA")))
                .andExpect(jsonPath("$.content[2].tipoMovimiento.nombre", is("ALTA")))
                .andExpect(jsonPath("$.content[2].sentido", is("ENTRADA")));
    }

    @Test
    void paginaLosMovimientosConSizePequeno() throws Exception {

        Long id = crearIngredienteConStock("Semillas de sesamo", 20); // ALTA

        mockMvc.perform(post("/ingredients/" + id + "/ingresos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cantidad\": 5}")); // COMPRA

        mockMvc.perform(get("/ingredients/" + id + "/movimientos").param("size", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)));
    }

    @Test
    void ingredienteSinMovimientosDevuelveListaVacia() throws Exception {

        Long id = crearIngredienteConStock("Pimenton dulce", 0);

        mockMvc.perform(get("/ingredients/" + id + "/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.empty", is(true)));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        mockMvc.perform(get("/ingredients/999999/movimientos"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }
}
