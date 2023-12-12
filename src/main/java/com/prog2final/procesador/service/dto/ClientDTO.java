package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    public ClientDTO() {}

    public ClientDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClientDTO{" + "id=" + id + '}';
    }
}
