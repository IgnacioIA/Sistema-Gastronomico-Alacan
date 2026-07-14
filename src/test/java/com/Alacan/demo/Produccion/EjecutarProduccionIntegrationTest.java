package com.Alacan.demo.Produccion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.application.ConsultarDisponibilidadService;
import com.Alacan.demo.Produccion.application.CrearRecetaService;
import com.Alacan.demo.Produccion.application.DetalleRecetaInput;
import com.Alacan.demo.Produccion.application.EjecutarProduccionService;
import com.Alacan.demo.Produccion.application.ListarMovimientosService;
import com.Alacan.demo.Produccion.application.ListarRecetasService;
import com.Alacan.demo.Produccion.application.ObtenerStockService;
import com.Alacan.demo.Produccion.application.ResultadoDisponibilidad;
import com.Alacan.demo.Produccion.domain.exception.StockInsuficienteException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.model.Sentido;
import com.Alacan.demo.Produccion.domain.model.Stock;
import com.Alacan.demo.Produccion.domain.model.TipoArticulo;
import com.Alacan.demo.Produccion.domain.model.UnidadMedida;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.StockRepository;
import com.Alacan.demo.Produccion.domain.repository.TipoArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.UnidadMedidaRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class EjecutarProduccionIntegrationTest {

    @Autowired
    private ArticuloRepository articuloRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;
    @Autowired
    private CrearRecetaService crearRecetaService;
    @Autowired
    private ConsultarDisponibilidadService consultarDisponibilidadService;
    @Autowired
    private EjecutarProduccionService ejecutarProduccionService;
    @Autowired
    private ObtenerStockService obtenerStockService;
    @Autowired
    private ListarMovimientosService listarMovimientosService;
    @Autowired
    private ListarRecetasService listarRecetasService;
    @Autowired
    private TipoArticuloRepository tipoArticuloRepository;

    private Articulo crearHarina() {
        TipoArticulo ingrediente = tipoArticuloRepository.buscarPorNombre("INGREDIENTE").orElseThrow();
        UnidadMedida kilogramo = buscarUnidad("KILOGRAMO");
        Articulo harina = new Articulo("Harina", ingrediente, kilogramo, null);
        articuloRepository.guardar(harina);
        stockRepository.guardar(new Stock(harina));
        return harina;
    }

    private Articulo crearPan() {
        TipoArticulo producto = tipoArticuloRepository.buscarPorNombre("PRODUCTO").orElseThrow();
        UnidadMedida unidad = buscarUnidad("UNIDAD");
        Articulo pan = new Articulo("Pan", producto, unidad, null);
        articuloRepository.guardar(pan);
        stockRepository.guardar(new Stock(pan));
        return pan;
    }

    private UnidadMedida buscarUnidad(String nombre) {
        return unidadMedidaRepository.buscarTodas().stream()
                .filter(u -> u.getNombre().equals(nombre))
                .findFirst()
                .orElseThrow();
    }

    // Carga stock inicial directamente sobre el agregado, sin pasar por un caso de uso de
    // aplicación: es un fixture de test, no el objeto bajo prueba (mismo criterio ya usado
    // para crear Articulo/Stock en este archivo).
    private void agregarStockInicial(Articulo articulo, BigDecimal cantidad) {
        Stock stock = stockRepository.buscarPorArticuloId(articulo.getId()).orElseThrow();
        stock.registrarIngreso(cantidad);
        stockRepository.guardar(stock);
    }

    private Receta crearRecetaPan(Articulo pan, Articulo harina) {
        // Receta base: con 2kg de harina se producen 10 panes.
        return crearRecetaService.ejecutar(
                pan.getId(),
                BigDecimal.valueOf(10),
                30,
                List.of(new DetalleRecetaInput(harina.getId(), BigDecimal.valueOf(2))));
    }

    @Test
    void produccionCompletaEsAtomicaYActualizaStockYMovimientos() {

        Articulo harina = crearHarina();
        Articulo pan = crearPan();
        Receta receta = crearRecetaPan(pan, harina);

        agregarStockInicial(harina, BigDecimal.valueOf(3));

        ResultadoDisponibilidad disponibilidad = consultarDisponibilidadService.ejecutar(receta.getId(), BigDecimal.valueOf(10));
        assertThat(disponibilidad.isDisponible()).isTrue();

        Stock stockPanActualizado = ejecutarProduccionService.ejecutar(receta.getId(), BigDecimal.valueOf(10));

        assertThat(stockPanActualizado.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(10));

        Stock stockHarina = obtenerStockService.ejecutar(harina.getId());
        assertThat(stockHarina.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(1));

        List<Movimiento> movimientosHarina = listarMovimientosService.ejecutar(harina.getId());
        assertThat(movimientosHarina).anyMatch(m -> m.getSentido() == Sentido.SALIDA
                && m.getCantidad().compareTo(BigDecimal.valueOf(2)) == 0);

        List<Movimiento> movimientosPan = listarMovimientosService.ejecutar(pan.getId());
        assertThat(movimientosPan).anyMatch(m -> m.getSentido() == Sentido.ENTRADA
                && m.getCantidad().compareTo(BigDecimal.valueOf(10)) == 0);
    }

    @Test
    void produccionFallaYNoModificaNadaSiFaltaStock() {

        Articulo harina = crearHarina();
        Articulo pan = crearPan();
        Receta receta = crearRecetaPan(pan, harina);

        agregarStockInicial(harina, BigDecimal.valueOf(1));

        ResultadoDisponibilidad disponibilidad = consultarDisponibilidadService.ejecutar(receta.getId(), BigDecimal.valueOf(10));
        assertThat(disponibilidad.isDisponible()).isFalse();
        assertThat(disponibilidad.getFaltantes()).hasSize(1);
        assertThat(disponibilidad.getFaltantes().get(0).getCantidadFaltante()).isEqualByComparingTo(BigDecimal.valueOf(1));

        assertThatThrownBy(() -> ejecutarProduccionService.ejecutar(receta.getId(), BigDecimal.valueOf(10)))
                .isInstanceOf(StockInsuficienteException.class);

        // Atomicidad: el stock de harina no debe haberse tocado tras el intento fallido.
        Stock stockHarina = obtenerStockService.ejecutar(harina.getId());
        assertThat(stockHarina.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(1));

        Stock stockPan = obtenerStockService.ejecutar(pan.getId());
        assertThat(stockPan.getCantidadActual()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void cantidadesSeEscalanProporcionalmenteALaRecetaBase() {

        Articulo harina = crearHarina();
        Articulo pan = crearPan();
        Receta receta = crearRecetaPan(pan, harina); // base: 2kg -> 10 panes

        agregarStockInicial(harina, BigDecimal.valueOf(4));

        // Producir 20 panes (2x la base) requiere 4kg de harina.
        Stock stockPan = ejecutarProduccionService.ejecutar(receta.getId(), BigDecimal.valueOf(20));

        assertThat(stockPan.getCantidadActual()).isEqualByComparingTo(BigDecimal.valueOf(20));

        Stock stockHarina = obtenerStockService.ejecutar(harina.getId());
        assertThat(stockHarina.getCantidadActual()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void unicaRecetaActivaPorArticuloDesactivaLaAnteriorAlCrearUnaNueva() {

        Articulo harina = crearHarina();
        Articulo pan = crearPan();
        Receta primeraReceta = crearRecetaPan(pan, harina);

        Receta segundaReceta = crearRecetaService.ejecutar(
                pan.getId(),
                BigDecimal.valueOf(5),
                15,
                List.of(new DetalleRecetaInput(harina.getId(), BigDecimal.valueOf(1))));

        List<Receta> todas = listarRecetasService.ejecutar();

        Receta primeraActualizada = todas.stream().filter(r -> r.getId().equals(primeraReceta.getId())).findFirst().orElseThrow();
        Receta segundaActualizada = todas.stream().filter(r -> r.getId().equals(segundaReceta.getId())).findFirst().orElseThrow();

        assertThat(primeraActualizada.isActivo()).isFalse();
        assertThat(segundaActualizada.isActivo()).isTrue();
    }
}
