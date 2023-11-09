package com.prog2final.procesador.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * ClientStocks entity.
 * Keeps track of how many stocks of each
 * company each client has at the current
 * moment.
 */
@Schema(description = "ClientStocks entity.\nKeeps track of how many stocks of each\ncompany each client has at the current\nmoment.")
@Entity
@Table(name = "client_stocks")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientStocks implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "stock_amount")
    private Float stockAmount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public ClientStocks id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getStockAmount() {
        return this.stockAmount;
    }

    public ClientStocks stockAmount(Float stockAmount) {
        this.setStockAmount(stockAmount);
        return this;
    }

    public void setStockAmount(Float stockAmount) {
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
            ", stockAmount=" + getStockAmount() +
            "}";
    }
}
