package com.prog2final.procesador.domain;

import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * OrderHistory entity.\nKeeps track of all the orders taken by\nthe procesor (whether yet processed or\nnot), with detailed information of the\norder, its current state and extra\nstatus information.
 */
@Schema(
    description = "OrderHistory entity.\nKeeps track of all the orders taken by\nthe procesor (whether yet processed or\nnot), with detailed information of the\norder, its current state and extra\nstatus information."
)
@Entity
@Table(name = "order_history")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "cliente", nullable = false)
    private Long cliente;

    @NotNull
    @Column(name = "accion_id", nullable = false)
    private Long accionId;

    @NotNull
    @Column(name = "accion", nullable = false)
    private String accion;

    @NotNull
    @Column(name = "operacion", nullable = false)
    private Boolean operacion;

    @Column(name = "cantidad")
    private Double cantidad;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "fecha_operacion")
    private Instant fechaOperacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "modo", nullable = false)
    private Modo modo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado;

    @Column(name = "operacion_observaciones")
    private String operacionObservaciones;

    @Column(name = "fecha_ejecucion")
    private Instant fechaEjecucion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderHistory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCliente() {
        return this.cliente;
    }

    public OrderHistory cliente(Long cliente) {
        this.setCliente(cliente);
        return this;
    }

    public void setCliente(Long cliente) {
        this.cliente = cliente;
    }

    public Long getAccionId() {
        return this.accionId;
    }

    public OrderHistory accionId(Long accionId) {
        this.setAccionId(accionId);
        return this;
    }

    public void setAccionId(Long accionId) {
        this.accionId = accionId;
    }

    public String getAccion() {
        return this.accion;
    }

    public OrderHistory accion(String accion) {
        this.setAccion(accion);
        return this;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Boolean getOperacion() {
        return this.operacion;
    }

    public OrderHistory operacion(Boolean operacion) {
        this.setOperacion(operacion);
        return this;
    }

    public void setOperacion(Boolean operacion) {
        this.operacion = operacion;
    }

    public Double getCantidad() {
        return this.cantidad;
    }

    public OrderHistory cantidad(Double cantidad) {
        this.setCantidad(cantidad);
        return this;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return this.precio;
    }

    public OrderHistory precio(Double precio) {
        this.setPrecio(precio);
        return this;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Instant getFechaOperacion() {
        return this.fechaOperacion;
    }

    public OrderHistory fechaOperacion(Instant fechaOperacion) {
        this.setFechaOperacion(fechaOperacion);
        return this;
    }

    public void setFechaOperacion(Instant fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    public Modo getModo() {
        return this.modo;
    }

    public OrderHistory modo(Modo modo) {
        this.setModo(modo);
        return this;
    }

    public void setModo(Modo modo) {
        this.modo = modo;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public OrderHistory estado(Estado estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getOperacionObservaciones() {
        return this.operacionObservaciones;
    }

    public OrderHistory operacionObservaciones(String operacionObservaciones) {
        this.setOperacionObservaciones(operacionObservaciones);
        return this;
    }

    public void setOperacionObservaciones(String operacionObservaciones) {
        this.operacionObservaciones = operacionObservaciones;
    }

    public Instant getFechaEjecucion() {
        return this.fechaEjecucion;
    }

    public OrderHistory fechaEjecucion(Instant fechaEjecucion) {
        this.setFechaEjecucion(fechaEjecucion);
        return this;
    }

    public void setFechaEjecucion(Instant fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderHistory)) {
            return false;
        }
        return id != null && id.equals(((OrderHistory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderHistory{" +
            "id=" + getId() +
            ", cliente=" + getCliente() +
            ", accionId=" + getAccionId() +
            ", accion='" + getAccion() + "'" +
            ", operacion='" + getOperacion() + "'" +
            ", cantidad=" + getCantidad() +
            ", precio=" + getPrecio() +
            ", fechaOperacion='" + getFechaOperacion() + "'" +
            ", modo='" + getModo() + "'" +
            ", estado='" + getEstado() + "'" +
            ", operacionObservaciones='" + getOperacionObservaciones() + "'" +
            ", fechaEjecucion='" + getFechaEjecucion() + "'" +
            "}";
    }
}
