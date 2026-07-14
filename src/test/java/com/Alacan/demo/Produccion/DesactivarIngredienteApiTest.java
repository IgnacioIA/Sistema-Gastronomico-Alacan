package com.Alacan.demo.Produccion;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

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

import com.Alacan.demo.Produccion.application.CrearRecetaService;
import com.Alacan.demo.Produccion.application.DetalleRecetaInput;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class DesactivarIngredienteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private TipoArticuloRepository tipoArticuloRepository;
    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;
    @Autowired
    private CrearRecetaService crearRecetaService;

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

    private UnidadMedida buscarUnidad(String nombre) {
        return unidadMedidaRepository.buscarTodas().stream()
                .filter(u -> u.getNombre().equals(nombre))
                .findFirst().orElseThrow();
    }

    @Test
    void desactivaUnIngredienteSinReferencias() throws Exception {

        Long id = crearIngrediente("Oregano");

        mockMvc.perform(post("/ingredients/" + id + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingrediente.activo", is(false)))
                .andExpect(jsonPath("$.recetasActivasQueLoUtilizan", is(0)));
    }

    @Test
    void desactivaIgualUnIngredienteUsadoEnUnaRecetaActiva() throws Exception {

        Long harinaId = crearIngrediente("Harina para receta");

        // Producto que requiere la harina como insumo (armado directo, no hay caso de uso
        // "crear producto" todavia - mismo patron que EjecutarProduccionIntegrationTest).
        TipoArticulo tipoProducto = tipoArticuloRepository.buscarPorNombre("PRODUCTO").orElseThrow();
        UnidadMedida unidad = buscarUnidad("UNIDAD");
        Articulo pan = new Articulo("Pan de prueba", tipoProducto, unidad, null);
        articuloRepository.guardar(pan);
        stockRepository.guardar(new Stock(pan));

        Articulo harina = articuloRepository.buscarPorId(harinaId).orElseThrow();
        crearRecetaService.ejecutar(pan.getId(), BigDecimal.TEN, 30,
                List.of(new DetalleRecetaInput(harina.getId(), BigDecimal.ONE)));

        mockMvc.perform(post("/ingredients/" + harinaId + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingrediente.activo", is(false)))
                .andExpect(jsonPath("$.recetasActivasQueLoUtilizan", is(1)));
    }

    @Test
    void desactivarDosVecesEsIdempotente() throws Exception {

        Long id = crearIngrediente("Comino");

        mockMvc.perform(post("/ingredients/" + id + "/desactivar")).andExpect(status().isOk());

        mockMvc.perform(post("/ingredients/" + id + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingrediente.activo", is(false)));
    }

    @Test
    void articuloInexistenteDevuelve404() throws Exception {

        mockMvc.perform(post("/ingredients/999999/desactivar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("ARTICLE_NOT_FOUND")));
    }
}
