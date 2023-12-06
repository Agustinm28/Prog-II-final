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

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
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
        List<Order> orders = orderRepository.findAll(filterByFields(cliente, accion, accion_id, operacion, estado, fechaInicio, fechaFin));

        if (orders == null) {
            return null;
        }

        return orders;
    }

    //* Filtrar por campos
    public static Specification<Order> filterByFields(
        String cliente,
        String accion,
        String accion_id,
        String operacion,
        String estado,
        String fechaInicio,
        String fechaFin
    ) {
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
            if (fechaInicio != null && fechaFin != null) {
                try {
                    // Formatear las fechas
                    List<ZonedDateTime> dates = formatDates(fechaInicio, fechaFin);

                    predicates.add(criteriaBuilder.between(root.get("fechaOperacion"), dates.get(0), dates.get(1)));
                } catch (Exception e) {
                    return null;
                }
            }
            if (fechaInicio != null && fechaFin == null) {
                try {
                    // Formatear las fechas
                    List<ZonedDateTime> dates = formatDates(fechaInicio, fechaFin);

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaOperacion"), dates.get(0)));
                } catch (Exception e) {
                    return null;
                }
            }
            if (fechaInicio == null && fechaFin != null) {
                try {
                    // Formatear las fechas
                    List<ZonedDateTime> dates = formatDates(fechaInicio, fechaFin);

                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaOperacion"), dates.get(0)));
                } catch (Exception e) {
                    return null;
                }
            }

            // Se retorna la lista de predicados para filtrar
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    //* Filtrar por rango de fechas
    private static List<ZonedDateTime> formatDates(String fechaInicio, String fechaFin) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Asignamos el patron de fecha a utilizar
            List<ZonedDateTime> dates = new ArrayList<>(); // Lista para almacenar las fechas finales

            if (fechaInicio != null) {
                if (!fechaInicio.contains(":")) {
                    // Si no lo tiene, agregar hora, minutos y segundos como 00:00:00
                    fechaInicio += " 00:00:00";
                }

                LocalDateTime dateTimefechaInicio = LocalDateTime.parse(fechaInicio, formatter); // Convertimos la fecha String a LocalDateTime
                ZonedDateTime fechaInicioZoned = dateTimefechaInicio.atZone(ZoneId.systemDefault()); // Convertimos la fecha LocalDateTime a ZonedDateTime (Para poder compararla con la fecha de la DB)

                dates.add(fechaInicioZoned);
            }

            if (fechaFin != null) {
                if (!fechaFin.contains(":")) {
                    // Si no lo tiene, agregar hora, minutos y segundos como 00:00:00
                    fechaFin += " 00:00:00";
                }

                LocalDateTime dateTimefechaFin = LocalDateTime.parse(fechaFin, formatter);
                ZonedDateTime fechaFinZoned = dateTimefechaFin.atZone(ZoneId.systemDefault());

                dates.add(fechaFinZoned);
            }

            log.info("{}Search: Date Range: {} to {}{}", ColorLogs.CYAN, fechaInicio, fechaFin, ColorLogs.RESET);

            return dates;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }
}
