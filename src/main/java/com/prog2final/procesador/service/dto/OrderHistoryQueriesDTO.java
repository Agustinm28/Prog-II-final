package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderHistoryQueriesDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<OrderHistoryQueryDTO> ordenesConsulta;

    public OrderHistoryQueriesDTO() {}

    public OrderHistoryQueriesDTO(List<OrderHistoryQueryDTO> orderHistories) {
        this.ordenesConsulta = orderHistories;
    }

    public List<OrderHistoryQueryDTO> getOrdenesConsulta() {
        return ordenesConsulta;
    }

    public void setOrdenesConsulta(List<OrderHistoryQueryDTO> orderHistories) {
        this.ordenesConsulta = orderHistories;
    }

    @Override
    public String toString() {
        return "OrderHistoriesDTO{" + "ordenesConsulta=" + ordenesConsulta + '}';
    }
}
