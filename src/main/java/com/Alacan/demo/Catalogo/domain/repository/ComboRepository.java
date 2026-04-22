package com.Alacan.demo.Catalogo.domain.repository;

import java.util.List;
import java.util.Optional;

import com.Alacan.demo.Catalogo.domain.model.Combo;

public interface ComboRepository {

    Optional<Combo> buscarPorId(Long id);

    List<Combo> buscarTodos();

    void guardar(Combo combo);
    
}
