package com.progii.finalogen.service;

import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.repository.OrderRepository;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class SearchServices {

    //? Clase para realizar busquedas mediante filtros sobre la base de datos //

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    public SearchServices(OrderRepository orderRepository, RestTemplate restTemplate2) {
        this.orderRepository = orderRepository;
    }

    //* Metodo para buscar por filtros en la DB
    public List<Order> searchByFilter(
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) String accion_id,
        @RequestParam(required = false) String operacion,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fechaInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String fechaFin
    ) {
        // Obtener todas las ordenes
        List<Order> orders = orderRepository.findAll();

        // Filtrar por clientes, accion, id de accion y operacion
        orders = filterByFields(orders, cliente, accion, accion_id, operacion, estado);

        // Filtrar por fecha
        orders = filterByDateRange(orders, fechaInicio, fechaFin);

        if (orders == null) {
            return null;
        }

        return orders;
    }

    //* Filtrar por campos
    private List<Order> filterByFields(
        List<Order> orders,
        String cliente,
        String accion,
        String accion_id,
        String operacion,
        String estado
    ) {
        List<Function<Order, Boolean>> conditions = new ArrayList<>();

        // Agregar condiciones al filtro
        conditions.add(order -> cliente == null || order.getCliente().toString().equals(cliente));
        conditions.add(order -> accion == null || order.getAccion().equals(accion));
        conditions.add(order -> accion_id == null || order.getAccionId().toString().equals(accion_id));
        conditions.add(order -> operacion == null || order.getOperacion().toString().equals(operacion));
        conditions.add(order -> estado == null || order.getEstado().toString().equals(estado));

        // Aplicar condiciones al stream
        for (Function<Order, Boolean> condition : conditions) {
            orders = orders.stream().filter(condition::apply).collect(Collectors.toList());
        }

        return orders;
    }

    //* Filtrar por rango de fechas
    private List<Order> filterByDateRange(List<Order> orders, String fechaInicio, String fechaFin) {
        if (fechaInicio != null && fechaFin != null) {
            try {
                // Verificar si la cadena contiene información de hora
                if (!fechaInicio.contains(":")) {
                    // Si no lo tiene, agregar hora, minutos y segundos como 00:00:00
                    fechaInicio += " 00:00:00";
                }
                if (!fechaFin.contains(":")) {
                    // Si no lo tiene, agregar hora, minutos y segundos como 00:00:00
                    fechaFin += " 00:00:00";
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Asignamos el patron de fecha a utilizar

                LocalDateTime dateTimefechaInicio = LocalDateTime.parse(fechaInicio, formatter); // Convertimos la fecha String a LocalDateTime
                LocalDateTime dateTimefechaFin = LocalDateTime.parse(fechaFin, formatter);

                ZonedDateTime fechaInicioZoned = dateTimefechaInicio.atZone(ZoneId.systemDefault()); // Convertimos la fecha LocalDateTime a ZonedDateTime (Para poder compararla con la fecha de la DB)
                ZonedDateTime fechaFinZoned = dateTimefechaFin.atZone(ZoneId.systemDefault());

                log.info("{}Search: Date Range: {} to {}{}", ColorLogs.CYAN, fechaInicio, fechaFin, ColorLogs.RESET);
                orders =
                    orders
                        .stream() // Filtramos las ordenes por fecha
                        .filter(order -> {
                            ZonedDateTime orderDate = order.getFechaOperacion();
                            return (
                                (orderDate.isEqual(fechaInicioZoned) || orderDate.isAfter(fechaInicioZoned)) &&
                                (orderDate.isEqual(fechaFinZoned) || orderDate.isBefore(fechaFinZoned))
                            );
                        })
                        .collect(Collectors.toList());
            } catch (Exception e) {
                log.error("Error: {}", e.getMessage());
                return null;
            }
        }

        return orders;
    }
}
