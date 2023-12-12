package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prog2final.procesador.domain.OrderHistory;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderHistoriesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("ordenes")
    private List<OrderHistoryDTO> ordenes;

    public OrderHistoriesDTO() {}

    public OrderHistoriesDTO(List<OrderHistoryDTO> orderHistories) {
        this.ordenes = orderHistories;
    }

    public List<OrderHistoryDTO> getOrderHistories() {
        return ordenes;
    }

    public void setOrderHistories(List<OrderHistoryDTO> orderHistories) {
        this.ordenes = orderHistories;
    }

    @Override
    public String toString() {
        return "OrderHistoriesDTO{" + "ordenes=" + ordenes + '}';
    }
}
