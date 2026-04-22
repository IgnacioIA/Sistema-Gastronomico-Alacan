package com.Alacan.demo.Catalogo.infraestructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Alacan.demo.Catalogo.domain.model.Combo;

@Repository
public interface ComboRepositoryJpa extends JpaRepository<Combo, Long>{
    
}
