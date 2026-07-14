package com.Alacan.demo.Produccion.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.TipoArticulo;

@Repository
public interface TipoArticuloRepositoryJpa extends JpaRepository<TipoArticulo, Long> {

    Optional<TipoArticulo> findByNombre(String nombre);
}
