package com.Alacan.demo.Produccion.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Alacan.demo.Produccion.domain.exception.ArticuloNoEncontradoException;
import com.Alacan.demo.Produccion.domain.model.Articulo;
import com.Alacan.demo.Produccion.domain.model.Receta;
import com.Alacan.demo.Produccion.domain.repository.ArticuloRepository;
import com.Alacan.demo.Produccion.domain.repository.RecetaRepository;

@Service
public class CrearRecetaService {

    private final RecetaRepository recetaRepository;
    private final ArticuloRepository articuloRepository;

    public CrearRecetaService(RecetaRepository recetaRepository, ArticuloRepository articuloRepository) {
        this.recetaRepository = recetaRepository;
        this.articuloRepository = articuloRepository;
    }

    @Transactional
    public Receta ejecutar(Long articuloProducidoId,
            BigDecimal cantidadProducida,
            Integer tiempoEstimadoMinutos,
            List<DetalleRecetaInput> detalles) {

        Articulo articuloProducido = buscarArticulo(articuloProducidoId);

        // Un articulo solo puede tener una receta activa a la vez (ADR 0001, seccion 11):
        // se desactiva la anterior en lugar de eliminarla, para no perder su historial.
        recetaRepository.buscarActivaPorArticuloProducido(articuloProducidoId)
                .ifPresent(recetaAnterior -> {
                    recetaAnterior.desactivar();
                    recetaRepository.guardar(recetaAnterior);
                });

        Receta receta = new Receta(articuloProducido, cantidadProducida, tiempoEstimadoMinutos);

        for (DetalleRecetaInput detalle : detalles) {
            Articulo articuloRequerido = buscarArticulo(detalle.getArticuloRequeridoId());
            receta.agregarDetalle(articuloRequerido, detalle.getCantidadRequerida());
        }

        recetaRepository.guardar(receta);

        return receta;
    }

    private Articulo buscarArticulo(Long articuloId) {
        return articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));
    }
}
