package com.progii.finalcom.service;

import com.progii.finalcom.aop.logging.ColorLogs;
import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.web.rest.SuccessfulOrdersResource;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AditionalServices {

    private static final Logger log = LoggerFactory.getLogger(SuccessfulOrdersResource.class);

    public static Map<String, List<Map<String, Object>>> formatList(List<SuccessfulOrders> successfulList) {
        try {
            List<Map<String, Object>> new_successfulOrders = new ArrayList<>();

            for (SuccessfulOrders successful : successfulList) {
                Map<String, Object> new_succesfulOrder = new LinkedHashMap<>();

                //DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

                ZonedDateTime date = successful.getFechaOperacion();

                //LocalDateTime local_date = date.toLocalDateTime();

                Instant date_time = date.toInstant();

                new_succesfulOrder.put("cliente", successful.getCliente());
                new_succesfulOrder.put("accionId", successful.getAccionId());
                new_succesfulOrder.put("accion", successful.getAccion());
                new_succesfulOrder.put("operacion", successful.getOperacion());
                new_succesfulOrder.put("cantidad", successful.getCantidad());
                new_succesfulOrder.put("precio", successful.getPrecio());
                new_succesfulOrder.put("fechaOperacion", date_time);
                new_succesfulOrder.put("modo", successful.getModo());
                new_succesfulOrder.put("operacionExitosa", successful.getOperacionExitosa());
                new_succesfulOrder.put("operacionObservaciones", successful.getOperacionObservaciones());

                new_successfulOrders.add(new_succesfulOrder);
            }

            Map<String, List<Map<String, Object>>> json_responseMap = new LinkedHashMap<>();

            json_responseMap.put("ordenes", new_successfulOrders);

            return json_responseMap;
        } catch (Exception e) {
            log.info("error: " + e.getMessage());
            return null;
        }
    }
}
