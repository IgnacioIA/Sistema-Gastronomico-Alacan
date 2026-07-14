package com.Alacan.demo.Produccion.api.dto;

public class DesactivarIngredienteResponse {

    private IngredienteResponse ingrediente;
    private long recetasActivasQueLoUtilizan;

    public IngredienteResponse getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(IngredienteResponse ingrediente) {
        this.ingrediente = ingrediente;
    }

    public long getRecetasActivasQueLoUtilizan() {
        return recetasActivasQueLoUtilizan;
    }

    public void setRecetasActivasQueLoUtilizan(long recetasActivasQueLoUtilizan) {
        this.recetasActivasQueLoUtilizan = recetasActivasQueLoUtilizan;
    }
}
