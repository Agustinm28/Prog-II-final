package com.progii.finalogen.service;

import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.repository.OrderRepository;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Transactional
public class SearchServices {

    //? Clase para realizar busquedas mediante filtros sobre la base de datos //

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    public SearchServices(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //* Metodo para buscar por filtros en la DB
    public List<Order> searchByFilter(
        @RequestParam(required = false) String cliente,
        @RequestParam(required = false) String accion,
        @RequestParam(required = false) String accion_id,
        @RequestParam(required = false) String operacion,
        @RequestParam(required = false) String estado,
        @RequestParam(required = false) String fechaInicio,
        @RequestParam(required = false) String fechaFin
    ) {
        // Obtener todas las ordenes que coincidan con los filtros
        List<Order> orders = orderRepository.findAll(filterByFields(cliente, accion, accion_id, operacion, estado));

        // Filtrar por fecha
        orders = filterByDateRange(orders, fechaInicio, fechaFin);

        if (orders == null) {
            return null;
        }

        return orders;
    }

    //* Filtrar por campos
    public static Specification<Order> filterByFields(String cliente, String accion, String accion_id, String operacion, String estado) {
        return (root, query, criteriaBuilder) -> {
            // Lista que almancena las condiciones de filtrado
            List<Predicate> predicates = new ArrayList<>();

            // Se aplican los filtros a la lista de predicados
            if (cliente != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente"), cliente));
            }
            if (accion != null) {
                predicates.add(criteriaBuilder.equal(root.get("accion"), accion));
            }
            if (accion_id != null) {
                predicates.add(criteriaBuilder.equal(root.get("accionId"), accion_id));
            }
            if (operacion != null) {
                try {
                    // Convertir el string a un enum del tipo Operacion
                    Operacion operacionEnum = Operacion.valueOf(operacion);
                    predicates.add(criteriaBuilder.equal(root.get("operacion"), operacionEnum));
                } catch (Exception e) {
                    return null;
                }
            }
            if (estado != null) {
                try {
                    // Convertir el string a un enum del tipo Estado
                    Estado estadoEnum = Estado.valueOf(estado);
                    predicates.add(criteriaBuilder.equal(root.get("estado"), estadoEnum));
                } catch (Exception e) {
                    return null;
                }
            }

            // Se retorna la lista de predicados para filtrar
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
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

                log.info("{}Search: Date Range: {} to {}{}", ColorLogs.CYAN, fechaInicioZoned, fechaFinZoned, ColorLogs.RESET);
                orders =
                    orders
                        .stream() // Filtramos las ordenes por fecha
                        .filter(order -> {
                            ZonedDateTime orderDate = order.getFechaOperacion();
                            return (orderDate.compareTo(fechaInicioZoned) >= 0 && orderDate.compareTo(fechaFinZoned) <= 0);
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
