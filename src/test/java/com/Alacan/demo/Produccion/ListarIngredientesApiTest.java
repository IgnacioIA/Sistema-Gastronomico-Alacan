package com.Alacan.demo.Produccion;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.application.CrearIngredienteService;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class ListarIngredientesApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CrearIngredienteService crearIngredienteService;

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    private Long kilogramoId;
    private Long gramoId;

    @BeforeEach
    void seedIngredientes() {

        kilogramoId = unidadMedidaRepository.buscarTodas().stream()
                .filter(u -> u.getNombre().equals("KILOGRAMO"))
                .findFirst().orElseThrow().getId();
        gramoId = unidadMedidaRepository.buscarTodas().stream()
                .filter(u -> u.getNombre().equals("GRAMO"))
                .findFirst().orElseThrow().getId();

        // Harina: con stock suficiente (no necesita reposición).
        crearIngredienteService.ejecutar("Harina 000", kilogramoId, null, BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        // Sal: por debajo del mínimo (necesita reposición).
        crearIngredienteService.ejecutar("Sal fina", gramoId, null, BigDecimal.valueOf(500), BigDecimal.valueOf(100));
        // Azúcar: sin stock físico.
        crearIngredienteService.ejecutar("Azúcar", gramoId, null, BigDecimal.valueOf(200), null);
    }

    @Test
    void listaPaginadaConFormaEsperada() throws Exception {

        mockMvc.perform(get("/ingredients").param("page", "0").param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", org.hamcrest.Matchers.is(0)))
                .andExpect(jsonPath("$.size", org.hamcrest.Matchers.is(15)))
                .andExpect(jsonPath("$.totalElements", org.hamcrest.Matchers.is(3)))
                .andExpect(jsonPath("$.empty", org.hamcrest.Matchers.is(false)))
                .andExpect(jsonPath("$.content.length()", org.hamcrest.Matchers.is(3)))
                .andExpect(jsonPath("$.content[0].unidadMedida.nombre", org.hamcrest.Matchers.notNullValue()))
                .andExpect(jsonPath("$.content[0].stock.necesitaReposicion", org.hamcrest.Matchers.notNullValue()));
    }

    @Test
    void buscaPorNombre() throws Exception {

        mockMvc.perform(get("/ingredients").param("search", "harina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", org.hamcrest.Matchers.is(1)))
                .andExpect(jsonPath("$.content[0].nombre", org.hamcrest.Matchers.is("Harina 000")));
    }

    @Test
    void filtraPorNecesitaReposicion() throws Exception {

        mockMvc.perform(get("/ingredients").param("inventario", "NECESITA_REPOSICION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", org.hamcrest.Matchers.is(1)))
                .andExpect(jsonPath("$.content[0].nombre", org.hamcrest.Matchers.is("Sal fina")));
    }

    @Test
    void filtraPorSinStock() throws Exception {

        mockMvc.perform(get("/ingredients").param("inventario", "SIN_STOCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", org.hamcrest.Matchers.is(1)))
                .andExpect(jsonPath("$.content[0].nombre", org.hamcrest.Matchers.is("Azúcar")));
    }

    @Test
    void ordenaPorNombreDescendente() throws Exception {

        mockMvc.perform(get("/ingredients").param("sort", "nombre,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre", org.hamcrest.Matchers.is("Sal fina")))
                .andExpect(jsonPath("$.content[2].nombre", org.hamcrest.Matchers.is("Azúcar")));
    }

    @Test
    void paginaConSizeUno() throws Exception {

        mockMvc.perform(get("/ingredients").param("size", "1").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", org.hamcrest.Matchers.is(1)))
                .andExpect(jsonPath("$.totalPages", org.hamcrest.Matchers.is(3)))
                .andExpect(jsonPath("$.first", org.hamcrest.Matchers.is(true)))
                .andExpect(jsonPath("$.last", org.hamcrest.Matchers.is(false)));
    }

    @Test
    void estadoInvalidoDevuelve400() throws Exception {

        mockMvc.perform(get("/ingredients").param("estado", "NO_EXISTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", org.hamcrest.Matchers.is("VALIDATION_ERROR")));
    }
}
