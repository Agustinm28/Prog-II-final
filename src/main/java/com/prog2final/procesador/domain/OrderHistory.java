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

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "operation_type")
    private Boolean operationType;

    @Column(name = "price")
    private Double price;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "execution_date")
    private Instant executionDate;

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

    public String getStockCode() {
        return this.stockCode;
    }

    public OrderHistory stockCode(String stockCode) {
        this.setStockCode(stockCode);
        return this;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
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

    public Double getPrice() {
        return this.price;
    }

    public OrderHistory price(Double price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return this.amount;
    }

    public OrderHistory amount(Double amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public OrderHistory creationDate(Instant creationDate) {
        this.setCreationDate(creationDate);
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getExecutionDate() {
        return this.executionDate;
    }

    public OrderHistory executionDate(Instant executionDate) {
        this.setExecutionDate(executionDate);
        return this;
    }

    public void setExecutionDate(Instant executionDate) {
        this.executionDate = executionDate;
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
            ", stockCode='" + getStockCode() + "'" +
            ", operationType='" + getOperationType() + "'" +
            ", price=" + getPrice() +
            ", amount=" + getAmount() +
            ", creationDate='" + getCreationDate() + "'" +
            ", executionDate='" + getExecutionDate() + "'" +
            ", mode='" + getMode() + "'" +
            ", state='" + getState() + "'" +
            ", info='" + getInfo() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
