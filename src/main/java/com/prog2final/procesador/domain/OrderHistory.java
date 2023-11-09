package com.prog2final.procesador.domain;

import com.prog2final.procesador.domain.enumeration.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
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

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "operation_type")
    private Boolean operationType;

    @Column(name = "price")
    private Float price;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "operation_date")
    private Instant operationDate;

    @Column(name = "mode")
    private String mode;

    @Column(name = "state")
    private String state;

    @Column(name = "info")
    private String info;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

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

    public Long getClientId() {
        return this.clientId;
    }

    public OrderHistory clientId(Long clientId) {
        this.setClientId(clientId);
        return this;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getStockId() {
        return this.stockId;
    }

    public OrderHistory stockId(Long stockId) {
        this.setStockId(stockId);
        return this;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Boolean getOperationType() {
        return this.operationType;
    }

    public OrderHistory operationType(Boolean operationType) {
        this.setOperationType(operationType);
        return this;
    }

    public void setOperationType(Boolean operationType) {
        this.operationType = operationType;
    }

    public Float getPrice() {
        return this.price;
    }

    public OrderHistory price(Float price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public OrderHistory amount(Integer amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Instant getOperationDate() {
        return this.operationDate;
    }

    public OrderHistory operationDate(Instant operationDate) {
        this.setOperationDate(operationDate);
        return this;
    }

    public void setOperationDate(Instant operationDate) {
        this.operationDate = operationDate;
    }

    public String getMode() {
        return this.mode;
    }

    public OrderHistory mode(String mode) {
        this.setMode(mode);
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getState() {
        return this.state;
    }

    public OrderHistory state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInfo() {
        return this.info;
    }

    public OrderHistory info(String info) {
        this.setInfo(info);
        return this;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Language getLanguage() {
        return this.language;
    }

    public OrderHistory language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
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
            ", clientId=" + getClientId() +
            ", stockId=" + getStockId() +
            ", operationType='" + getOperationType() + "'" +
            ", price=" + getPrice() +
            ", amount=" + getAmount() +
            ", operationDate='" + getOperationDate() + "'" +
            ", mode='" + getMode() + "'" +
            ", state='" + getState() + "'" +
            ", info='" + getInfo() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
