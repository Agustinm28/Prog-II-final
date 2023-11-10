package com.progii.finalogen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.web.rest.errors.BadRequestAlertException;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class AditionalOrderServices {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static String url = "http://192.168.194.254:8000/api/";

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    // Method to check if a client exists
    public Map clientExists(Integer id) {
        String endpoint = url + "clientes";

        log.debug("{}Request to check if client exists : {}{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        List<Map<String, Object>> clientes = response.getBody();

        for (Map<String, Object> cliente : clientes) {
            if (cliente.get("id") == id) {
                log.debug("{}Client {} exists{}", ColorLogs.GREEN, cliente, ColorLogs.RESET);
                return cliente;
            }
        }

        return null;
    }

    // Method to check if an accion exists
    public Map accionExists(Integer id) {
        log.debug("{}Request to check if an accion exists : {}{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

        String endpoint = url + "acciones/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
        );

        List<Map<String, Object>> acciones = response.getBody().get("acciones");

        for (Map<String, Object> accion : acciones) {
            if (accion.get("id") == id) {
                log.debug("{}Accion {} exists{}", ColorLogs.GREEN, accion, ColorLogs.RESET);
                return accion;
            }
        }

        return null;
    }

    // Method to get the last value of an accion in the market
    public Float getlastValue(String accion) {
        log.debug("{}Request to check last value of an accion : {}{}", ColorLogs.PURPLE, accion, ColorLogs.RESET);

        String endpoint = url + "acciones/ultimovalor/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

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
                Map<String, Object> lastValue = (Map<String, Object>) responseBody.get("ultimoValor");

                if (lastValue != null) {
                    Object value = lastValue.get("valor");
                    log.debug("{}Last value of accion {} is {}{}", ColorLogs.CYAN, accion, value, ColorLogs.RESET);
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

        return null;
    }

    // Method to check if client is in the list of clients that have actions, and if it is, check if the client has the action
    public int sellClientExists(Integer id, String accionName) {
        //TODO Cambiar por endpoint a servicio complementario

        // Read JSON file (temporally)
        String filePath = "sellData.json";
        JsonNode dataNode = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            dataNode = jsonNode.get("data");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // TODO FIN

        // Check if clientId is in the list of clients that have actions in dataNode
        // If it is, check if the client has the action

        for (JsonNode clienteNode : dataNode) {
            int clienteId = clienteNode.get("clienteId").asInt();
            String clienteName = clienteNode.get("cliente").asText();

            if (clienteId == id) {
                log.debug("{}Client {} has shares{}", ColorLogs.GREEN, clienteName, ColorLogs.RESET);
                // Check if the client has the action
                JsonNode acciones = clienteNode.get("acciones");

                // Iterar sobre las acciones del cliente TODO: NO ANDA!!!!
                for (Iterator<Map.Entry<String, JsonNode>> it = acciones.fields(); it.hasNext();) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    String accion = entry.getKey();
                    int cantidadAccion = entry.getValue().asInt();

                    if (accion.equals(accionName)) {
                        log.debug(
                            "{}Client {} has {} of share {}",
                            ColorLogs.GREEN,
                            clienteName,
                            cantidadAccion,
                            accionName,
                            ColorLogs.RESET
                        );
                        return cantidadAccion;
                    } else {
                        log.debug("{}Client {} does not have share {}", ColorLogs.RED, clienteName, accionName, ColorLogs.RESET);
                        return 0;
                    }
                }
            }
        }

        return 0;
    }

    // Method to review an order, where we check if the client exists, if the accion exists, if the price is correct and if the date is correct
    public Order reviewOrder(Order order) {
        // Check if client exists
        Map checkClient = clientExists(order.getCliente());

        if (checkClient == null) {
            throw new BadRequestAlertException("Client does not exist", "order", "clientnotfound");
        }

        // Check if action exists
        Map checkAccion = accionExists(order.getAccionId());

        if (checkAccion == null) {
            throw new BadRequestAlertException("Action does not exist", "order", "accionnotfound");
        }

        // Get operacion parameter
        Operacion operacion = order.getOperacion();
        if (operacion == Operacion.VENTA) {
            log.debug("{}Checking client shares{}", ColorLogs.CYAN, ColorLogs.RESET);
            // Check if client has the action
            int cantidadAccion = sellClientExists(order.getCliente(), order.getAccion());

            if (cantidadAccion == 0) {
                throw new BadRequestAlertException("Client does not have shares to sell", "order", "clientnotshares");
            } else if (cantidadAccion < order.getCantidad()) {
                throw new BadRequestAlertException("Client does not have enough shares to sell", "order", "clientnotenoughshares");
            }
        }

        // Take the action code from checkAccion and set it to order if is null or different to checkAccion
        if (order.getAccion() == null || order.getAccion() != checkAccion.get("codigo")) {
            order.accion((String) checkAccion.get("codigo"));
        }

        // Set the price
        float lastValue = (float) getlastValue(order.getAccion());
        log.debug("{}Last value of accion {} is {}{}", ColorLogs.GREEN, order.getAccion(), lastValue, ColorLogs.RESET);
        order.setPrecio(lastValue);

        // Set actual time if null
        if (order.getFechaOperacion() == null) {
            ZonedDateTime now = ZonedDateTime.now();
            String formatteDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
            order.setFechaOperacion(ZonedDateTime.parse(formatteDate));
        }

        return order;
    }
}
