package com.progii.finalcom.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progii.finalcom.domain.SuccessfulOrders;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class OrderReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private ZonedDateTime fechaOperacion;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private List<SuccessfulOrders> ordenes;

    // Constructor vacío
    public OrderReportDTO() {}

    // Constructor con argumentos
    public OrderReportDTO(List<SuccessfulOrders> ordenes) {
        this.ordenes = ordenes;
    }

    // Setters y getters
    public List<SuccessfulOrders> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<SuccessfulOrders> ordenes) {
        this.ordenes = ordenes;
    }
}
