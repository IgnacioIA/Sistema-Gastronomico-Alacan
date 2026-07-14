package com.Alacan.demo.Produccion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.Alacan.demo.Produccion.domain.exception.ArticuloInactivoException;
import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.RecetaNoEncontradaException;
import com.Alacan.demo.Produccion.domain.exception.StockInsuficienteException;
import com.Alacan.demo.Produccion.domain.exception.TipoArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.TipoMovimientoNoEncontradoException;
import com.Alacan.demo.Produccion.domain.exception.UnidadMedidaBloqueadaException;
import com.Alacan.demo.Produccion.domain.exception.UnidadMedidaNoEncontradaException;

// RFC 7807 (Problem Details) para todas las excepciones del dominio Produccion.
// "code" es el identificador estable que el Frontend usa para personalizar la UI;
// nunca debe depender del texto de "detail", que es solo para debugging humano.
@RestControllerAdvice(basePackages = "com.Alacan.demo.Produccion.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(UnidadMedidaNoEncontradaException.class)
    public ProblemDetail handleUnidadMedidaNoEncontrada(UnidadMedidaNoEncontradaException ex) {
        return problemDetail(HttpStatus.NOT_FOUND, "Unidad de medida no encontrada", ex.getMessage(),
                "UNIDAD_MEDIDA_NOT_FOUND");
    }

    @ExceptionHandler(ArticuloNoEncontradoException.class)
    public ProblemDetail handleArticuloNoEncontrado(ArticuloNoEncontradoException ex) {
        return problemDetail(HttpStatus.NOT_FOUND, "Artículo no encontrado", ex.getMessage(), "ARTICLE_NOT_FOUND");
    }

    @ExceptionHandler(RecetaNoEncontradaException.class)
    public ProblemDetail handleRecetaNoEncontrada(RecetaNoEncontradaException ex) {
        return problemDetail(HttpStatus.NOT_FOUND, "Receta no encontrada", ex.getMessage(), "RECIPE_NOT_FOUND");
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ProblemDetail handleStockInsuficiente(StockInsuficienteException ex) {
        return problemDetail(HttpStatus.CONFLICT, "Stock insuficiente", ex.getMessage(), "INSUFFICIENT_STOCK");
    }

    @ExceptionHandler(UnidadMedidaBloqueadaException.class)
    public ProblemDetail handleUnidadMedidaBloqueada(UnidadMedidaBloqueadaException ex) {
        return problemDetail(HttpStatus.CONFLICT, "Unidad de medida bloqueada", ex.getMessage(),
                "UNIDAD_MEDIDA_BLOQUEADA");
    }

    @ExceptionHandler(ArticuloInactivoException.class)
    public ProblemDetail handleArticuloInactivo(ArticuloInactivoException ex) {
        return problemDetail(HttpStatus.CONFLICT, "Artículo inactivo", ex.getMessage(), "ARTICULO_INACTIVO");
    }

    @ExceptionHandler({ TipoArticuloNoEncontradoException.class, TipoMovimientoNoEncontradoException.class })
    public ProblemDetail handleConfiguracionFaltante(RuntimeException ex) {
        // No es un error del cliente: significa que falta un dato de seed
        // (TipoArticulo/TipoMovimiento) que el sistema da por garantizado.
        return problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error de configuración interna", ex.getMessage(),
                "INTERNAL_CONFIGURATION_ERROR");
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ProblemDetail handleValidacion(RuntimeException ex) {
        return problemDetail(HttpStatus.BAD_REQUEST, "Solicitud inválida", ex.getMessage(), "VALIDATION_ERROR");
    }

    private ProblemDetail problemDetail(HttpStatus status, String title, String detail, String code) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("code", code);
        return problemDetail;
    }
}
