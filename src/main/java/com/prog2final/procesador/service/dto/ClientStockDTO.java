package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientStockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cliente;
    private Long accionId;
    private Double cantidadActual;

    public ClientStockDTO() {}

    public ClientStockDTO(Long cliente, Long accionId, Double cantidadActual) {
        this.cliente = cliente;
        this.accionId = accionId;
        this.cantidadActual = cantidadActual;
    }

    public Long getCliente() {
        return cliente;
    }

    public void setCliente(Long cliente) {
        this.cliente = cliente;
    }

    public Long getAccionId() {
        return accionId;
    }

    public void setAccionId(Long accionId) {
        this.accionId = accionId;
    }

    public Double getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(Double cantidadActual) {
        this.cantidadActual = cantidadActual;
    }
}
