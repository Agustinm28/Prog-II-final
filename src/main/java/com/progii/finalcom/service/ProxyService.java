package com.progii.finalcom.service;

import com.progii.finalcom.web.rest.errors.BadRequestAlertException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.lang.Collections;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@Service
@Transactional
public class ProxyService {

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    //Clientes
    public Map<String, List<Map<String, Object>>> getDataMapClientes(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    //Clientes Buscar
    public Object getDataMapClientesBuscar(String endpoint, String nombre, String empresa) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endpoint)
            .queryParamIfPresent("nombre", Optional.ofNullable(nombre))
            .queryParamIfPresent("empresa", Optional.ofNullable(empresa));

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            // Extraer el cuerpo
            Map<String, List<Map<String, Object>>> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("clientes")) {
                return responseBody.get("clientes");
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
