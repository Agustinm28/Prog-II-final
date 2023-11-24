package com.progii.finalogen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.repository.OrderRepository;
import com.progii.finalogen.web.rest.errors.BadRequestAlertException;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
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
                if (cliente.get("id").equals(id)) {
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

    public List<Order> searchByFilter( //! Modificar despues para incluir filtrado por estado
        List<Order> orders,
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) String accion_id,
        @RequestParam(required = false) String operacion,
        @RequestParam(required = false) String estado
    ) {
        if (cliente != null && accion != null && accion_id != null && operacion != null) {
            log.info("{}Search by ClientID, ShareID, Share & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order ->
                        order.getCliente().toString().equals(cliente) &&
                        order.getAccion().equals(accion) &&
                        order.getAccionId().toString().equals(accion_id) &&
                        order.getOperacion().toString().equals(operacion)
                    )
                    .collect(Collectors.toList());
        }
        if (cliente != null && accion != null && accion_id != null) {
            log.info("{}Search by ClientID, ShareID & Share{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order ->
                        order.getCliente().toString().equals(cliente) &&
                        order.getAccion().equals(accion) &&
                        order.getAccionId().toString().equals(accion_id)
                    )
                    .collect(Collectors.toList());
        }
        if (cliente != null && accion != null && operacion != null) {
            log.info("{}Search by ClientID, Share & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order ->
                        order.getCliente().toString().equals(cliente) &&
                        order.getAccion().equals(accion) &&
                        order.getOperacion().toString().equals(operacion)
                    )
                    .collect(Collectors.toList());
        }
        if (cliente != null && accion_id != null && operacion != null) {
            log.info("{}Search by ClientID, ShareID & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order ->
                        order.getCliente().toString().equals(cliente) &&
                        order.getAccionId().toString().equals(accion_id) &&
                        order.getOperacion().toString().equals(operacion)
                    )
                    .collect(Collectors.toList());
        }
        if (accion != null && accion_id != null && operacion != null) {
            log.info("{}Search by ShareID, Share & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order ->
                        order.getAccion().equals(accion) &&
                        order.getAccionId().toString().equals(accion_id) &&
                        order.getOperacion().toString().equals(operacion)
                    )
                    .collect(Collectors.toList());
        }
        if (cliente != null && accion != null) {
            log.info("{}Search by ClientID & Share{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getCliente().toString().equals(cliente) && order.getAccion().equals(accion))
                    .collect(Collectors.toList());
        }
        if (cliente != null && accion_id != null) {
            log.info("{}Search by ClientID & ShareID{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getCliente().toString().equals(cliente) && order.getAccionId().toString().equals(accion_id))
                    .collect(Collectors.toList());
        }
        if (cliente != null && operacion != null) {
            log.info("{}Search by ClientID & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getCliente().toString().equals(cliente) && order.getOperacion().toString().equals(operacion))
                    .collect(Collectors.toList());
        }
        if (accion != null && accion_id != null) {
            log.info("{}Search by ShareID & Share{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getAccion().equals(accion) && order.getAccionId().toString().equals(accion_id))
                    .collect(Collectors.toList());
        }
        if (accion != null && operacion != null) {
            log.info("{}Search by Share & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getAccion().equals(accion) && order.getOperacion().toString().equals(operacion))
                    .collect(Collectors.toList());
        }
        if (accion_id != null && operacion != null) {
            log.info("{}Search by ShareID & Operation{}", ColorLogs.CYAN, ColorLogs.RESET);
            orders =
                orders
                    .stream()
                    .filter(order -> order.getAccionId().toString().equals(accion_id) && order.getOperacion().toString().equals(operacion))
                    .collect(Collectors.toList());
        }
        if (cliente != null) {
            log.info("{}Search: ClientID: {}{}", ColorLogs.CYAN, cliente, ColorLogs.RESET);
            orders = orders.stream().filter(order -> order.getCliente().toString().equals(cliente)).collect(Collectors.toList());
        }
        if (accion != null) {
            log.info("{}Search: Share: {}{}", ColorLogs.CYAN, accion, ColorLogs.RESET);
            orders = orders.stream().filter(order -> order.getAccion().equals(accion)).collect(Collectors.toList());
        }
        if (accion_id != null) {
            log.info("{}Search: ShareID: {}{}", ColorLogs.CYAN, accion_id, ColorLogs.RESET);
            orders = orders.stream().filter(order -> order.getAccionId().toString().equals(accion_id)).collect(Collectors.toList());
        }
        if (operacion != null) {
            log.info("{}Search: Operation: {}{}", ColorLogs.CYAN, operacion, ColorLogs.RESET);
            orders = orders.stream().filter(order -> order.getOperacion().toString().equals(operacion)).collect(Collectors.toList());
        }
        if (estado != null) {
            log.info("{}Search: Status: {}{}", ColorLogs.CYAN, estado, ColorLogs.RESET);
            orders = orders.stream().filter(order -> order.getEstado().toString().equals(estado)).collect(Collectors.toList());
        }

        return orders;
    }

    public List<Map<String, Object>> formatList(List<Order> orders) {
        // Crear lista de diccionarios nueva
        List<Map<String, Object>> new_orders = new ArrayList<>();

        // Eliminar primer y ultimo elemento de cada diccionario
        for (Order order : orders) {
            Map<String, Object> new_order = new LinkedHashMap<>();
            //new_order.put("id", order.getId());
            new_order.put("cliente", order.getCliente());
            new_order.put("accionId", order.getAccionId());
            new_order.put("accion", order.getAccion());
            new_order.put("operacion", order.getOperacion());
            new_order.put("precio", order.getPrecio());
            new_order.put("cantidad", order.getCantidad());
            new_order.put("fechaOperacion", order.getFechaOperacion());
            new_order.put("modo", order.getModo());
            //new_order.put("estado", order.getEstado());
            new_orders.add(new_order);
        }

        log.info("{}List of orders: {}{}", ColorLogs.CYAN, new_orders, ColorLogs.RESET);

        return new_orders;
    }

    // Method to check if client is in the list of clients that have actions, and if it is, check if the client has the action
    public int sellClientExists(Integer id, Integer sharesId) {
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
                Map<String, Object> responseBody = response.getBody();

                log.info("{}{}{}", ColorLogs.PURPLE, responseBody, ColorLogs.RESET);

                if (Objects.nonNull(responseBody)) {
                    Integer actualQuantity = (Integer) responseBody.get("cantidadActual");
                    String shareName = (String) responseBody.get("accion");

                    if (actualQuantity != null) {
                        log.info(
                            "{}Shares of client with ID {} for {} are {}{}",
                            ColorLogs.CYAN,
                            id,
                            shareName,
                            actualQuantity,
                            ColorLogs.RESET
                        );

                        // Convert actualQuantity to int
                        if (actualQuantity instanceof Integer) {
                            // If it is a Integer, return it
                            return (Integer) actualQuantity;
                        } else if (actualQuantity instanceof Number) {
                            // If it is a Number, convert it to Integer
                            return ((Number) actualQuantity).intValue();
                        }
                    }
                }
                log.warn("{}Client with ID {} does not have shares with ID {}{}", ColorLogs.YELLOW, id, sharesId, ColorLogs.RESET);
                return 0;
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return 0;
        }
        return 0;
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
            int cantidadAccion = sellClientExists(order.getCliente(), order.getAccionId());

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

        //* Put status as PENDIENTE
        order.setEstado(Estado.PENDIENTE);

        return order;
    }
}
