package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.Receta;

@Repository
public interface RecetaRepositoryJpa extends JpaRepository<Receta, Long> {

    Optional<Receta> findByArticuloProducidoIdAndActivoTrue(Long articuloId);

    @Query("SELECT COUNT(DISTINCT d.receta) FROM RecetaDetalle d "
            + "WHERE d.articuloRequerido.id = :articuloId AND d.receta.activo = true")
    long contarRecetasActivasQueRequierenArticulo(@Param("articuloId") Long articuloId);
}
