package com.progii.finalogen.service;

import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.web.rest.errors.BadRequestAlertException;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class ReviewOrderService {

    //? Clase utilizada para comprobar la integridad de la orden realizada //

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final DataServices dataServices;
    private final AditionalOrderServices aditionalOrderServices;

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    public ReviewOrderService(DataServices dataServices, AditionalOrderServices aditionalOrderServices) {
        this.dataServices = dataServices;
        this.aditionalOrderServices = aditionalOrderServices;
    }

    //* Metodo para revisar una orden, donde se verifica en conjunto que el cliente exista, que la accion exista, que el precio sea correcto y la fecha sea correcta, entre otras comprobaciones
    public Order reviewOrder(Order order) {
        // 1. Verificar que el cliente exista
        Map<String, Object> checkClient = aditionalOrderServices.clientExists(order.getCliente());

        if (checkClient == null) {
            throw new BadRequestAlertException("Client does not exist", "order", "clientnotfound");
        }

        // 2. Verificar que la accion exista
        Map<String, Object> checkAccion = aditionalOrderServices.accionExists(order.getAccionId());

        if (checkAccion == null) {
            throw new BadRequestAlertException("Action does not exist", "order", "accionnotfound");
        }

        // 3. Verificar que el ID de la accion sea el mismo que el codigo de la accion, caso contrario, cambiarlo
        if (order.getAccion() == null || order.getAccion() != checkAccion.get("codigo")) {
            order.accion((String) checkAccion.get("codigo"));
        }

        // 4. Verificar que la cantidad de acciones de la peticion sea mayor a 0 en orden de comprar/vender
        if (order.getCantidad() <= 0) {
            throw new BadRequestAlertException("A valid quantity to buy/sell must be specified.", "order", "quantitynotvalid");
        }

        // 5. Verificar el tipo de operacion
        Operacion operacion = order.getOperacion();
        // 5.1. Si es venta, verificar que el cliente tenga acciones para vender
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

            // 5.2. Verificar que el cliente tenga acciones para vender
            int cantidadAccion = aditionalOrderServices.sellClientExists(order.getCliente(), order.getAccionId());

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

        // 6. Verificar el ultimo valor del precio de la accion
        float lastValue = (float) dataServices.getLastValue(order.getAccion());
        order.setPrecio(lastValue);

        // 7 Verificar la fecha de la operacion, si no esta definida, definirla como la fecha actual
        if (order.getFechaOperacion() == null) {
            ZonedDateTime now = ZonedDateTime.now();
            String formatteDate = now.format(DateTimeFormatter.ISO_DATE_TIME);
            order.setFechaOperacion(ZonedDateTime.parse(formatteDate));
        }

        // 8 Poner estado de la operacion como PENDIENTE, en orden de ser procesada
        order.setEstado(Estado.PENDIENTE);

        return order; // Devolver la orden con las verificaciones y cambios realizados
    }
}
