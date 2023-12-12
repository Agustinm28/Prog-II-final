package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String codigo;

    public StockDTO() {}

    public StockDTO(Long id, String codigo) {
        this.id = id;
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return "StockDTO{" + "id=" + id + ", codigo='" + codigo + '\'' + '}';
    }
}
