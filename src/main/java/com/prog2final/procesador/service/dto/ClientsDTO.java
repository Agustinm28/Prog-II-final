package com.prog2final.procesador.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("clientes")
    private List<ClientDTO> clientes;

    public ClientsDTO() {}

    public ClientsDTO(List<ClientDTO> clients) {
        this.clientes = clients;
    }

    public List<ClientDTO> getClients() {
        return clientes;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clientes = clients;
    }

    @Override
    public String toString() {
        return "ClientsDTO{" + "clientes=" + clientes + '}';
    }
}
