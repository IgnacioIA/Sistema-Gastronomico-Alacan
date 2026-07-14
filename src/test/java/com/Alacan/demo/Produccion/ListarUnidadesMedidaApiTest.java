package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class ListarUnidadesMedidaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void devuelveElCatalogoCompletoSinPaginar() throws Exception {

        mockMvc.perform(get("/measurement-units"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(5)))
                .andExpect(jsonPath("$[*].nombre", hasItem("KILOGRAMO")))
                .andExpect(jsonPath("$[*].abreviatura", hasItem("kg")))
                .andExpect(jsonPath("$[0].id").exists())
                // Es un array plano, no un envelope de paginacion.
                .andExpect(jsonPath("$.content").doesNotExist());
    }
}
