package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import com.prog2final.procesador.domain.enumeration.Operacion;
import java.io.Serializable;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cliente;

    private Long accionId;

    private String accion;

    private Operacion operacion;

    private Double cantidad;

    private Double precio;

    private Instant fechaOperacion;

    private Modo modo;

    private Boolean operacionExitosa;

    private String operacionObservaciones;

    public OrderHistoryDTO() {}

    public OrderHistoryDTO(
        Long cliente,
        Long accionId,
        String accion,
        Operacion operacion,
        Double cantidad,
        Double precio,
        Instant fechaOperacion,
        Modo modo,
        Boolean operacionExitosa,
        String operacionObservaciones
    ) {
        this.cliente = cliente;
        this.accionId = accionId;
        this.accion = accion;
        this.operacion = operacion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fechaOperacion = fechaOperacion;
        this.modo = modo;
        this.operacionExitosa = operacionExitosa;
        this.operacionObservaciones = operacionObservaciones;
    }

    public OrderHistoryDTO cliente(Long cliente) {
        this.cliente = cliente;
        return this;
    }

    public OrderHistoryDTO accionId(Long accionId) {
        this.accionId = accionId;
        return this;
    }

    public OrderHistoryDTO accion(String accion) {
        this.accion = accion;
        return this;
    }

    public OrderHistoryDTO operacion(Operacion operacion) {
        this.operacion = operacion;
        return this;
    }

    public OrderHistoryDTO cantidad(Double cantidad) {
        this.cantidad = cantidad;
        return this;
    }

    public OrderHistoryDTO precio(Double precio) {
        this.precio = precio;
        return this;
    }

    public OrderHistoryDTO fechaOperacion(Instant fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
        return this;
    }

    public OrderHistoryDTO modo(Modo modo) {
        this.modo = modo;
        return this;
    }

    public OrderHistoryDTO operacionExitosa(Boolean operacionExitosa) {
        this.operacionExitosa = operacionExitosa;
        return this;
    }

    public OrderHistoryDTO operacionObservaciones(String operacionObservaciones) {
        this.operacionObservaciones = operacionObservaciones;
        return this;
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

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Operacion getOperacion() {
        return operacion;
    }

    public void setOperacion(Operacion operacion) {
        this.operacion = operacion;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Instant getFechaOperacion() {
        return fechaOperacion;
    }

    public void setFechaOperacion(Instant fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Modo getModo() {
        return modo;
    }

    public void setModo(Modo modo) {
        this.modo = modo;
    }

    public Boolean getOperacionExitosa() {
        return operacionExitosa;
    }

    public void setOperacionExitosa(Boolean operacionExitosa) {
        this.operacionExitosa = operacionExitosa;
    }

    public String getOperacionObservaciones() {
        return operacionObservaciones;
    }

    public void setOperacionObservaciones(String operacionObservaciones) {
        this.operacionObservaciones = operacionObservaciones;
    }

    @Override
    public String toString() {
        return (
            "OrderHistoryDTO{" +
            "cliente=" +
            cliente +
            ", accionId=" +
            accionId +
            ", accion='" +
            accion +
            '\'' +
            ", operacion=" +
            operacion +
            ", cantidad=" +
            cantidad +
            ", precio=" +
            precio +
            ", fechaOperacion=" +
            fechaOperacion +
            ", modo=" +
            modo +
            ", operacionExitosa=" +
            operacionExitosa +
            ", operacionObservaciones='" +
            operacionObservaciones +
            '\'' +
            '}'
        );
    }
}
