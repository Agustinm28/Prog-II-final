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

        log.info("{}Request to check if client with ID {} exists{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
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
                    log.info("{}Client {} with id {} exists{}", ColorLogs.GREEN, cliente.get("nombreApellido"), id, ColorLogs.RESET);
                    return cliente;
                }
            }

            log.warn("{}Client with ID {} does not exist{}", ColorLogs.YELLOW, id, ColorLogs.RESET);
            return null;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    // Method to check if an accion exists
    public Map accionExists(Integer id) {
        log.info("{}Request to check if share with ID {} exists{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

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

            List<Map<String, Object>> acciones = response.getBody().get("acciones");

            for (Map<String, Object> accion : acciones) {
                if (accion.get("id") == id) {
                    log.info("{}Share {} with id {} exists{}", ColorLogs.GREEN, accion.get("codigo"), accion.get("id"), ColorLogs.RESET);
                    return accion;
                }
            }

            log.warn("{}Share with ID {} does not exist{}", ColorLogs.YELLOW, id, ColorLogs.RESET);
            return null;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    // Method to get the last value of an accion in the market
    public Float getlastValue(String accion) {
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
                    Map<String, Object> lastValue = (Map<String, Object>) responseBody.get("ultimoValor");

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

    // Method to check if client is in the list of clients that have actions, and if it is, check if the client has the action
    public int sellClientExists(Integer id, String accionName) {
        log.info("{}Request to check if client with id {} has shares of {}{}", ColorLogs.PURPLE, id, accionName, ColorLogs.RESET);

        try {
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
                    log.info("{}Client {} has shares{}", ColorLogs.GREEN, clienteName, ColorLogs.RESET);
                    // Check if the client has the action
                    JsonNode acciones = clienteNode.get("acciones");

                    // log.debug("{}Shares of client {} are {}{}", ColorLogs.CYAN, clienteName, acciones, ColorLogs.RESET);

                    // Iterar sobre las acciones del cliente
                    for (Iterator<Map.Entry<String, JsonNode>> it = acciones.fields(); it.hasNext();) {
                        Map.Entry<String, JsonNode> entry = it.next();
                        String accion = entry.getKey();
                        int cantidadAccion = entry.getValue().asInt();

                        if (accion.equals(accionName)) {
                            log.info(
                                "{}Client {} has {} shares for {}()",
                                ColorLogs.GREEN,
                                clienteName,
                                cantidadAccion,
                                accionName,
                                ColorLogs.RESET
                            );
                            return cantidadAccion;
                        }
                    }
                    log.warn("{}Client {} does not have shares for {}{}", ColorLogs.YELLOW, clienteName, accionName, ColorLogs.RESET);
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return 0;
        }
    }

    // Method to review an order, where we check if the client exists, if the accion exists, if the price is correct and if the date is correct
    public Order reviewOrder(Order order) {
        //* Check if client exists
        Map checkClient = clientExists(order.getCliente());

        if (checkClient == null) {
            throw new BadRequestAlertException("Client does not exist", "order", "clientnotfound");
        }

        //* Check if action exists
        Map checkAccion = accionExists(order.getAccionId());

        if (checkAccion == null) {
            throw new BadRequestAlertException("Action does not exist", "order", "accionnotfound");
        }

        // Take the action code from checkAccion and set it to order if is null or different to checkAccion
        if (order.getAccion() == null || order.getAccion() != checkAccion.get("codigo")) {
            order.accion((String) checkAccion.get("codigo"));
        }

        // If the quantity to buy/sell is 0 or less, throw an error
        if (order.getCantidad() <= 0) {
            throw new BadRequestAlertException("A valid quantity to buy/sell must be specified.", "order", "quantitynotvalid");
        }

        //* Get parameter "operacion" to check if it is a buy or a sell, and check if the client has the action if it is a sell
        Operacion operacion = order.getOperacion();
        if (operacion == Operacion.VENTA) {
            log.info("{}Checking client shares{}", ColorLogs.CYAN, ColorLogs.RESET);
            log.info(
                "{}Client with ID {} wants to sell {} shares of {}{}",
                ColorLogs.CYAN,
                order.getCliente(),
                order.getCantidad(),
                order.getAccion(),
                ColorLogs.RESET
            );

            // Check if client has the action
            int cantidadAccion = sellClientExists(order.getCliente(), order.getAccion());

            if (cantidadAccion == 0) {
                throw new BadRequestAlertException(
                    "Client does not have shares of " + order.getAccion() + " to sell",
                    "order",
                    "clientnotshares"
                );
            } else if (cantidadAccion < order.getCantidad()) {
                throw new BadRequestAlertException(
                    "Client does not have enough shares of " + order.getAccion() + " to sell",
                    "order",
                    "clientnotenoughshares"
                );
            }
        }

        //* Set the price
        float lastValue = (float) getlastValue(order.getAccion());
        order.setPrecio(lastValue);

        //* Set actual order time if null
        if (order.getFechaOperacion() == null) {
            ZonedDateTime now = ZonedDateTime.now();
            String formatteDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
            order.setFechaOperacion(ZonedDateTime.parse(formatteDate));
        }

        return order;
    }
}
