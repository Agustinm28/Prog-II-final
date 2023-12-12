package com.progii.finalogen.service;

import com.progii.finalogen.aop.logging.ColorLogs;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class DataServices {

    //? Clase para traer datos de servicio de catedra / complementarios //

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Value("${urls.servicio-catedra}") // Obtengo la url del servicio de catedra del application.yml
    public String url;

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    //* Metodo para traer clientes del servicio de catedra
    public List<Map<String, Object>> getClients() {
        // Set endpoint
        String endpoint = url + "clientes/";

        log.info("{}Request to get clients{}", ColorLogs.PURPLE, ColorLogs.RESET);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            List<Map<String, Object>> clientes = response.getBody().get("clientes"); // Obtengo la lista de clientes

            return clientes;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    //* Metodo para traer acciones del servicio de catedra
    public List<Map<String, Object>> getAcciones() {
        // Set endpoint
        String endpoint = url + "acciones/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            List<Map<String, Object>> acciones = response.getBody().get("acciones"); // Obtengo la lista de acciones

            return acciones;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    //* Metodo para obtener el ultimo valor de una accion del servicio de catedra
    public Float getLastValue(String accion) {
        log.info("{}Request to check last value for share: {}{}", ColorLogs.PURPLE, accion, ColorLogs.RESET);

        String endpoint = url + "acciones/ultimovalor/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                endpoint + accion,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                if (responseBody != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> lastValue = (Map<String, Object>) responseBody.get("ultimoValor"); // Obtengo el ultimo valor de la accion

                    if (lastValue != null) {
                        Object value = lastValue.get("valor");
                        log.info("{}Last value for share {} is {}{}", ColorLogs.GREEN, accion, value, ColorLogs.RESET);
                        if (value instanceof Float) {
                            // If it is a Float, return it
                            return (Float) value;
                        } else if (value instanceof Number) {
                            // If it is a Number, convert it to Float
                            return ((Number) value).floatValue();
                        }
                    }
                }
            }

            log.warn("{}Last value for share {} not found{}", ColorLogs.YELLOW, accion, ColorLogs.RESET);
            return null;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    //* Metodo para obtener la cantidad de acciones de un cliente del servicio de catedra
    public Map<String, Object> getClientShares(Integer id, Integer sharesId) {
        log.info("{}Request to check if client with ID {} has shares with ID {}{}", ColorLogs.PURPLE, id, sharesId, ColorLogs.RESET);

        String endpoint = url + "reporte-operaciones/consulta_cliente_accion?clienteId=" + id + "&accionId=" + sharesId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> clientData = response.getBody(); // Obtengo los datos del cliente

                return clientData;
            }

            return null;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }
}
