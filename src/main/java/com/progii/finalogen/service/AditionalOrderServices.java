package com.progii.finalogen.service;

import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class AditionalOrderServices {

    //? Clase utilizada para realizar verificaciones de datos, formato, entre otras sobre las ordenes realizadas //

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderService orderService;
    private final DataServices dataServices;
    private final SearchServices searchServices;

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    public AditionalOrderServices(OrderService orderService, DataServices dataServices, SearchServices searchServices) {
        this.orderService = orderService;
        this.dataServices = dataServices;
        this.searchServices = searchServices;
    }

    //* 1. Metodo para verificar si un cliente existe
    public Map<String, Object> clientExists(Integer id) {
        log.info("{}Checking if client with ID {} exists{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

        try {
            // Obtengo la lista de clientes de DataServices
            List<Map<String, Object>> clientes = dataServices.getClients();

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

    //* 2. Metodo para verificar si una accion existe
    public Map<String, Object> accionExists(Integer id) {
        log.info("{}Checking if share with ID {} exists{}", ColorLogs.PURPLE, id, ColorLogs.RESET);

        try {
            List<Map<String, Object>> acciones = dataServices.getAcciones();

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

    //* 3. Metodo para formatear la lista de ordenes en orden de devolverlas al procesador en un formato especifico
    public Map<String, List<Map<String, Object>>> formatList(List<Order> orders) {
        try {
            // Crear lista de diccionarios nueva
            List<Map<String, Object>> new_orders = new ArrayList<>();

            // Eliminar primer y ultimo elemento de cada diccionario
            for (Order order : orders) {
                Map<String, Object> new_order = new LinkedHashMap<>(); // LinkedHashMap para mantener el orden de los elementos
                new_order.put("cliente", order.getCliente());
                new_order.put("accionId", order.getAccionId());
                new_order.put("accion", order.getAccion());
                new_order.put("operacion", order.getOperacion());
                new_order.put("precio", order.getPrecio());
                new_order.put("cantidad", order.getCantidad());
                new_order.put("fechaOperacion", order.getFechaOperacion());
                new_order.put("modo", order.getModo());
                new_orders.add(new_order);
            }

            log.info("{}List of orders: {}{}", ColorLogs.CYAN, new_orders, ColorLogs.RESET);

            Map<String, List<Map<String, Object>>> responseList = new LinkedHashMap<>();
            responseList.put("ordenes", new_orders);

            return responseList; // Devolver la lista de diccionarios formateada
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }

    //* 4. Metodo para cancelar una orden
    public Order cancelOrder(Long id) {
        // 1. Buscar las ordenes con estado pendiente
        List<Order> orders = searchServices.searchByFilter(null, null, null, null, "PENDIENTE", null, null);
        // 2. Verificar si la order existe en orders
        for (Order order : orders) {
            if (order.getId().equals(id)) {
                // 3. Cambiar estado a CANCELADA
                order.setEstado(Estado.CANCELADO);
                orderService.update(order); // Actualizar la orden
                return order;
            }
        }

        return null;
    }

    //* 5. Metodo para verificar si un cliente tiene acciones para vender
    public int sellClientExists(Integer id, Integer sharesId) {
        log.info("{}Cheking if client with ID {} has shares with ID {}{}", ColorLogs.PURPLE, id, sharesId, ColorLogs.RESET);

        try {
            Map<String, Object> responseBody = dataServices.getClientShares(id, sharesId); // Obtengo los datos del cliente

            if (Objects.nonNull(responseBody)) {
                Integer actualQuantity = (Integer) responseBody.get("cantidadActual"); // Obtengo la cantidad de acciones del cliente
                String shareName = (String) responseBody.get("accion"); // Obtengo el nombre de la accion

                if (actualQuantity != null) {
                    log.info(
                        "{}Shares of client with ID {} for {} are {}{}",
                        ColorLogs.CYAN,
                        id,
                        shareName,
                        actualQuantity,
                        ColorLogs.RESET
                    );

                    // Convertir actualQuantity a Integer
                    if (actualQuantity instanceof Integer) {
                        // Si es Integer, devolverlo
                        return (Integer) actualQuantity;
                    } else if (actualQuantity instanceof Number) {
                        // Si es Number, convertirlo a Integer
                        return ((Number) actualQuantity).intValue();
                    }
                }
            }
            log.warn("{}Client with ID {} does not have shares with ID {}{}", ColorLogs.YELLOW, id, sharesId, ColorLogs.RESET);
            return 0;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return 0;
        }
    }
}
