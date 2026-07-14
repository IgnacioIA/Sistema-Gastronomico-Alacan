package com.Alacan.demo.Produccion.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Produccion.domain.model.UnidadMedida;

@Repository
public interface UnidadMedidaRepositoryJpa extends JpaRepository<UnidadMedida, Long> {

}
