package com.progii.finalgen.service;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class AditionalOrderServices {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    // Check if client exists method
    public Map clientExists(Integer id) {
        log.debug("Request to check if client exists : {}", id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            "http://192.168.194.254:8000/api/clientes",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> clientes = response.getBody();

        for (Map<String, Object> cliente : clientes) {
            if (cliente.get("id") == id) {
                return cliente;
            }
        }

        return null;
    }

    public Map accionExists(Integer id) {
        log.debug("Request to check if an accion exists : {}", id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
            "http://192.168.194.254:8000/api/acciones/",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
        );

        List<Map<String, Object>> acciones = response.getBody().get("acciones");

        for (Map<String, Object> accion : acciones) {
            if (accion.get("id") == id) {
                return accion;
            }
        }

        return null;
    }

    public Map reviewOrder(Map<String, Object> order) {
        // Programar resto de logica y comprobaciones

        return null;
    }
}
