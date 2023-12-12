package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StocksDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("acciones")
    private List<StockDTO> acciones;

    public StocksDTO() {}

    public StocksDTO(List<StockDTO> stocks) {
        this.acciones = stocks;
    }

    public List<StockDTO> getStocks() {
        return acciones;
    }

    public void setStocks(List<StockDTO> stocks) {
        this.acciones = stocks;
    }

    @Override
    public String toString() {
        return "StocksDTO{" + "acciones=" + acciones + '}';
    }
}
