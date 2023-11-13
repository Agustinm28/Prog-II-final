package com.prog2final.procesador.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ClientStocks.
 */
@Entity
@Table(name = "client_stocks")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientStocks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_amount")
    private Double stockAmount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClientStocks id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public ClientStocks clientId(Long clientId) {
        this.setClientId(clientId);
        return this;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getStockCode() {
        return this.stockCode;
    }

    public ClientStocks stockCode(String stockCode) {
        this.setStockCode(stockCode);
        return this;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Double getStockAmount() {
        return this.stockAmount;
    }

    public ClientStocks stockAmount(Double stockAmount) {
        this.setStockAmount(stockAmount);
        return this;
    }

    public void setStockAmount(Double stockAmount) {
        this.stockAmount = stockAmount;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientStocks)) {
            return false;
        }
        return id != null && id.equals(((ClientStocks) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientStocks{" +
            "id=" + getId() +
            ", clientId=" + getClientId() +
            ", stockCode='" + getStockCode() + "'" +
            ", stockAmount=" + getStockAmount() +
            "}";
    }
}
