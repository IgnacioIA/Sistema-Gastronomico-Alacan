package com.Alacan.demo.Produccion.api.controller;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Alacan.demo.Produccion.api.dto.ActualizarIngredienteRequest;
import com.Alacan.demo.Produccion.api.dto.CrearIngredienteRequest;
import com.Alacan.demo.Produccion.api.dto.DesactivarIngredienteResponse;
import com.Alacan.demo.Produccion.api.dto.IngredienteListItemResponse;
import com.Alacan.demo.Produccion.api.dto.IngredienteResponse;
import com.Alacan.demo.Produccion.api.dto.MovimientoResponse;
import com.Alacan.demo.Produccion.api.dto.PaginaResponse;
import com.Alacan.demo.Produccion.api.dto.RegistrarCantidadStockRequest;
import com.Alacan.demo.Produccion.api.dto.RegistrarCorreccionStockRequest;
import com.Alacan.demo.Produccion.api.mapper.IngredienteMapper;
import com.Alacan.demo.Produccion.api.mapper.MovimientoMapper;
import com.Alacan.demo.Produccion.application.ActualizarIngredienteService;
import com.Alacan.demo.Produccion.application.CrearIngredienteService;
import com.Alacan.demo.Produccion.application.DesactivarIngredienteResultado;
import com.Alacan.demo.Produccion.application.DesactivarIngredienteService;
import com.Alacan.demo.Produccion.application.IngredienteConStockResultado;
import com.Alacan.demo.Produccion.application.ListarIngredientesService;
import com.Alacan.demo.Produccion.application.ListarMovimientosService;
import com.Alacan.demo.Produccion.application.RegistrarCorreccionStockService;
import com.Alacan.demo.Produccion.application.RegistrarIngresoStockService;
import com.Alacan.demo.Produccion.application.RegistrarPerdidaStockService;
import com.Alacan.demo.Produccion.domain.model.Movimiento;
import com.Alacan.demo.Produccion.domain.query.ArticuloConStock;
import com.Alacan.demo.Produccion.domain.query.CampoOrdenIngrediente;
import com.Alacan.demo.Produccion.domain.query.Direccion;
import com.Alacan.demo.Produccion.domain.query.FiltroInventario;
import com.Alacan.demo.Produccion.domain.query.FiltroListaArticulos;
import com.Alacan.demo.Produccion.domain.query.OrdenIngrediente;
import com.Alacan.demo.Produccion.domain.query.Paginacion;
import com.Alacan.demo.Produccion.domain.query.ResultadoPaginado;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/ingredients")
public class IngredienteController {

    private final CrearIngredienteService crearIngredienteService;
    private final ListarIngredientesService listarIngredientesService;
    private final ActualizarIngredienteService actualizarIngredienteService;
    private final DesactivarIngredienteService desactivarIngredienteService;
    private final RegistrarIngresoStockService registrarIngresoStockService;
    private final RegistrarPerdidaStockService registrarPerdidaStockService;
    private final RegistrarCorreccionStockService registrarCorreccionStockService;
    private final ListarMovimientosService listarMovimientosService;

    public IngredienteController(CrearIngredienteService crearIngredienteService,
            ListarIngredientesService listarIngredientesService,
            ActualizarIngredienteService actualizarIngredienteService,
            DesactivarIngredienteService desactivarIngredienteService,
            RegistrarIngresoStockService registrarIngresoStockService,
            RegistrarPerdidaStockService registrarPerdidaStockService,
            RegistrarCorreccionStockService registrarCorreccionStockService,
            ListarMovimientosService listarMovimientosService) {
        this.crearIngredienteService = crearIngredienteService;
        this.listarIngredientesService = listarIngredientesService;
        this.actualizarIngredienteService = actualizarIngredienteService;
        this.desactivarIngredienteService = desactivarIngredienteService;
        this.registrarIngresoStockService = registrarIngresoStockService;
        this.registrarPerdidaStockService = registrarPerdidaStockService;
        this.registrarCorreccionStockService = registrarCorreccionStockService;
        this.listarMovimientosService = listarMovimientosService;
    }

    @PostMapping
    public ResponseEntity<IngredienteResponse> crearIngrediente(@RequestBody CrearIngredienteRequest request) {

        BigDecimal cantidadInicial = request.getStockInicial() != null
                ? request.getStockInicial().getCantidad()
                : null;

        IngredienteConStockResultado resultado = crearIngredienteService.ejecutar(
                request.getNombre(),
                request.getUnidadMedidaId(),
                request.getDescripcion(),
                request.getStockMinimo(),
                cantidadInicial);

        IngredienteResponse response = IngredienteMapper.toResponse(resultado);

        return ResponseEntity.created(URI.create("/ingredients/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredienteResponse> actualizarIngrediente(
            @PathVariable Long id,
            @RequestBody ActualizarIngredienteRequest request) {

        IngredienteConStockResultado resultado = actualizarIngredienteService.ejecutar(
                id,
                request.getNombre(),
                request.getDescripcion(),
                request.getUnidadMedidaId(),
                request.getStockMinimo());

        return ResponseEntity.ok(IngredienteMapper.toResponse(resultado));
    }

    @PostMapping("/{id}/desactivar")
    public ResponseEntity<DesactivarIngredienteResponse> desactivarIngrediente(@PathVariable Long id) {

        DesactivarIngredienteResultado resultado = desactivarIngredienteService.ejecutar(id);

        return ResponseEntity.ok(IngredienteMapper.toResponse(resultado));
    }

    @PostMapping("/{id}/ingresos")
    public ResponseEntity<IngredienteResponse> registrarIngresoStock(
            @PathVariable Long id,
            @RequestBody RegistrarCantidadStockRequest request) {

        IngredienteConStockResultado resultado = registrarIngresoStockService.ejecutar(id, request.getCantidad());

        return ResponseEntity.ok(IngredienteMapper.toResponse(resultado));
    }

    @PostMapping("/{id}/perdidas")
    public ResponseEntity<IngredienteResponse> registrarPerdidaStock(
            @PathVariable Long id,
            @RequestBody RegistrarCantidadStockRequest request) {

        IngredienteConStockResultado resultado = registrarPerdidaStockService.ejecutar(id, request.getCantidad());

        return ResponseEntity.ok(IngredienteMapper.toResponse(resultado));
    }

    @PostMapping("/{id}/correcciones")
    public ResponseEntity<IngredienteResponse> registrarCorreccionStock(
            @PathVariable Long id,
            @RequestBody RegistrarCorreccionStockRequest request) {

        IngredienteConStockResultado resultado = registrarCorreccionStockService.ejecutar(id, request.getCantidadReal());

        return ResponseEntity.ok(IngredienteMapper.toResponse(resultado));
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<PaginaResponse<MovimientoResponse>> listarMovimientos(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        ResultadoPaginado<Movimiento> resultado = listarMovimientosService.ejecutar(id, new Paginacion(page, size));

        return ResponseEntity.ok(MovimientoMapper.toPaginaResponse(resultado));
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<IngredienteListItemResponse>> listarIngredientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "nombre,asc") String sort,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String inventario,
            @RequestParam(required = false) Long unidadMedidaId) {

        FiltroListaArticulos filtro = new FiltroListaArticulos(
                search,
                parseEstado(estado),
                unidadMedidaId,
                parseInventario(inventario));

        ResultadoPaginado<ArticuloConStock> resultado = listarIngredientesService.ejecutar(
                filtro,
                parseOrden(sort),
                new Paginacion(page, size));

        return ResponseEntity.ok(IngredienteMapper.toPaginaResponse(resultado));
    }

    private Boolean parseEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            return null;
        }

        return switch (estado.toUpperCase()) {
            case "ACTIVO" -> Boolean.TRUE;
            case "INACTIVO" -> Boolean.FALSE;
            default -> throw new IllegalArgumentException("Estado inválido: " + estado);
        };
    }

    private FiltroInventario parseInventario(String inventario) {

        if (inventario == null || inventario.isBlank()) {
            return null;
        }

        try {
            return FiltroInventario.valueOf(inventario.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Filtro de inventario inválido: " + inventario);
        }
    }

    private OrdenIngrediente parseOrden(String sort) {

        String[] partes = sort.split(",");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato de sort inválido, se espera 'campo,direccion': " + sort);
        }

        CampoOrdenIngrediente campo = switch (partes[0].trim().toLowerCase()) {
            case "nombre" -> CampoOrdenIngrediente.NOMBRE;
            case "cantidadactual" -> CampoOrdenIngrediente.CANTIDAD_ACTUAL;
            case "cantidaddisponible" -> CampoOrdenIngrediente.CANTIDAD_DISPONIBLE;
            case "stockminimo" -> CampoOrdenIngrediente.STOCK_MINIMO;
            case "necesitareposicion" -> CampoOrdenIngrediente.NECESITA_REPOSICION;
            default -> throw new IllegalArgumentException("Campo de ordenamiento inválido: " + partes[0]);
        };

        Direccion direccion = switch (partes[1].trim().toLowerCase()) {
            case "asc" -> Direccion.ASC;
            case "desc" -> Direccion.DESC;
            default -> throw new IllegalArgumentException("Dirección de ordenamiento inválida: " + partes[1]);
        };

        return new OrdenIngrediente(campo, direccion);
    }
}
