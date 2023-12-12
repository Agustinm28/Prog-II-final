package com.prog2final.procesador.service;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prog2final.procesador.config.ApplicationProperties;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import com.prog2final.procesador.domain.enumeration.Operacion;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import com.prog2final.procesador.service.dto.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private ApplicationProperties appProperties;

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private final OrderHistoryRepository orderHistoryRepository;

    private JSONRequester jsonRequester;

    private Clock internalClock;

    public OrderService(OrderHistoryRepository orderHistoryRepository, JSONRequester jsonRequester) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.jsonRequester = jsonRequester;
        this.internalClock = Clock.systemUTC();
    }

    public void setJsonRequester(JSONRequester jsonRequester) {
        this.jsonRequester = jsonRequester;
    }

    public void setInternalClock(Clock internalClock) {
        this.internalClock = internalClock;
    }

    @Scheduled(initialDelay = 20, fixedRate = 1000)
    @Transactional
    public List<OrderHistory> getAndPersistNewOrders() {
        OrderHistoriesDTO orders = null;
        try {
            orders =
                objectMapper.readValue(
                    jsonRequester.getJSONFromEndpoint(
                        appProperties.getGenerator().getUrl(),
                        appProperties.getGenerator().getOrdersEndpoint(),
                        appProperties.getGenerator().getToken()
                    ),
                    OrderHistoriesDTO.class
                );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ArrayList<OrderHistory> requestedOrders = new ArrayList<>();

        for (OrderHistoryDTO ordDTO : orders.getOrdenes()) {
            try {
                OrderHistory ord = new OrderHistory()
                    .cliente(ordDTO.getCliente())
                    .accionId(ordDTO.getAccionId())
                    .accion(ordDTO.getAccion())
                    .operacion(ordDTO.getOperacion())
                    .cantidad(ordDTO.getCantidad())
                    .precio(ordDTO.getPrecio())
                    .fechaOperacion(ordDTO.getFechaOperacion())
                    .modo(ordDTO.getModo())
                    .estado(Estado.PENDIENTE)
                    .operacionObservaciones("Esperando procesamiento...")
                    .reportada(false);
                orderHistoryRepository.save(ord);
                requestedOrders.add(ord);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("Se obtuvieron y persistieron {} nueva/s orden/es desde el generador de órdenes.", requestedOrders.size());
        return requestedOrders;
    }

    @Transactional
    public List<OrderHistory> performProcessing(List<OrderHistory> orders) {
        List<OrderHistory> successfulOrders = new ArrayList<>();
        for (OrderHistory order : orders) {
            try {
                List<Object> processingResult = verifyIfValidOrder(order);
                if (processingResult.get(0).equals(false)) {
                    order.estado(Estado.FALLIDA).fechaEjecucion(internalClock.instant());
                } else {
                    order.estado(Estado.EXITOSA).fechaEjecucion(internalClock.instant());
                    successfulOrders.add(order);
                }
                order.operacionObservaciones(processingResult.get(1).toString());
                orderHistoryRepository.save(order);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return successfulOrders;
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Etc/UTC")
    @Transactional
    public List<OrderHistory> processStartOfDayOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacionAsc(
            Modo.PRINCIPIODIA,
            Estado.PENDIENTE
        );
        List<OrderHistory> successfulOrders = performProcessing(orders);
        log.debug(
            "Procesando las órdenes 'PRINCIPIODIA': se encontró/aron {} orden/es, de las cuales {} resultó/aron exitosa/s y {} falló/aron.",
            orders.size(),
            successfulOrders.size(),
            orders.size() - successfulOrders.size()
        );
        return successfulOrders;
    }

    @Scheduled(cron = "0 59 17 * * ?", zone = "Etc/UTC")
    @Transactional
    public List<OrderHistory> processEndOfDayOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacionAsc(Modo.FINDIA, Estado.PENDIENTE);
        List<OrderHistory> successfulOrders = performProcessing(orders);
        log.debug(
            "Procesando las órdenes 'FINDIA': se encontró/aron {} orden/es, de las cuales {} resultó/aron exitosa/s y {} falló/aron.",
            orders.size(),
            successfulOrders.size(),
            orders.size() - successfulOrders.size()
        );
        return successfulOrders;
    }

    @Scheduled(initialDelay = 50, fixedRate = 1000)
    @Transactional
    public List<OrderHistory> processInstantOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacionAsc(Modo.AHORA, Estado.PENDIENTE);
        List<OrderHistory> successfulOrders = performProcessing(orders);
        log.debug(
            "Procesando las órdenes 'AHORA': se encontró/aron {} orden/es, de las cuales {} resultó/aron exitosa/s y {} falló/aron.",
            orders.size(),
            successfulOrders.size(),
            orders.size() - successfulOrders.size()
        );
        return successfulOrders;
    }

    @Transactional
    public List<Object> verifyIfValidOrder(OrderHistory order) throws JsonProcessingException {
        Integer currentHour = internalClock.instant().atZone(internalClock.getZone()).getHour();
        ArrayList<Object> result = new ArrayList<>();
        if (currentHour < 9 || currentHour > 18) {
            result.add(false);
            result.add(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
            return result;
        }

        ClientsDTO clients = objectMapper.readValue(
            jsonRequester.getJSONFromEndpoint(
                appProperties.getCompServices().getUrl(),
                appProperties.getCompServices().getClientsEndpoint(),
                appProperties.getCompServices().getToken()
            ),
            ClientsDTO.class
        );
        HashSet<Long> uniqueClientIds = new HashSet<>();

        for (ClientDTO client : clients.getClients()) {
            try {
                uniqueClientIds.add(client.getId());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        StocksDTO stocks = objectMapper.readValue(
            jsonRequester.getJSONFromEndpoint(
                appProperties.getCompServices().getUrl(),
                appProperties.getCompServices().getStocksEndpoint(),
                appProperties.getCompServices().getToken()
            ),
            StocksDTO.class
        );
        HashSet<Long> uniqueStockIds = new HashSet<>();
        HashSet<String> uniqueStockCodes = new HashSet<>();

        for (StockDTO stock : stocks.getStocks()) {
            try {
                uniqueStockIds.add(stock.getId());
                uniqueStockCodes.add(stock.getCodigo());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (
            !(
                uniqueClientIds.contains(order.getCliente()) &&
                uniqueStockIds.contains(order.getAccionId()) &&
                uniqueStockCodes.contains(order.getAccion())
            )
        ) {
            result.add(false);
            result.add(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
            return result;
        }

        ClientStockDTO clientStock = objectMapper.readValue(
            jsonRequester.getJSONFromEndpoint(
                appProperties.getCompServices().getUrl(),
                appProperties.getCompServices().getClientStockEndpoint() +
                String.format("?clienteId=%s&accionId=%s", order.getCliente(), order.getAccionId()),
                appProperties.getCompServices().getToken()
            ),
            ClientStockDTO.class
        );
        double stockAmount = clientStock.getCantidadActual() == null ? 0D : clientStock.getCantidadActual();

        if (order.getOperacion().equals(Operacion.VENTA) && stockAmount < order.getCantidad()) {
            result.add(false);
            result.add(
                "No hay suficientes acciones para vender (el cliente solicitado no posee la cantidad de acciones necesaria para proceder con la operación)."
            );
            return result;
        }

        result.add(true);
        result.add("OK.");
        return result;
    }

    @Transactional
    @Scheduled(fixedRate = 30000)
    public void reportOrders() {
        List<OrderHistory> successfulOrders = orderHistoryRepository.findAllByEstadoAndReportada(Estado.EXITOSA, false);
        List<OrderHistory> failedOrders = orderHistoryRepository.findAllByEstadoAndReportada(Estado.FALLIDA, false);

        List<OrderHistory> allOrders = Stream.concat(successfulOrders.stream(), failedOrders.stream()).collect(Collectors.toList());
        ArrayList<OrderHistoryDTO> ordersToReport = new ArrayList<>();

        for (OrderHistory ord : allOrders) {
            OrderHistoryDTO obj = new OrderHistoryDTO()
                .cliente(ord.getCliente())
                .accionId(ord.getAccionId())
                .accion(ord.getAccion())
                .operacion(ord.getOperacion())
                .cantidad(ord.getCantidad())
                .precio(ord.getPrecio())
                .fechaOperacion(ord.getFechaOperacion())
                .modo(ord.getModo())
                .operacionExitosa(ord.getEstado().equals(Estado.EXITOSA))
                .operacionObservaciones(ord.getOperacionObservaciones());
            ordersToReport.add(obj);
        }

        try {
            OrderHistoriesDTO fullBody = new OrderHistoriesDTO();
            fullBody.setOrdenes(ordersToReport);
            String fullBodyString = objectMapper.writeValueAsString(fullBody);

            System.out.printf("LO QUE BUSCABAS: %s", fullBodyString);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest
                .newBuilder(
                    URI.create(
                        appProperties.getCompServices().getUrl() +
                        appProperties.getCompServices().getReportsEndpoints().getReportsEndpoint()
                    )
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + appProperties.getCompServices().getToken())
                .timeout(Duration.of(10, SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(fullBodyString))
                .build();

            int successfullyReported = 0;

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                for (OrderHistory order : allOrders) {
                    order.reportada(true);
                    orderHistoryRepository.save(order);
                    successfullyReported++;
                }
            }
            orderHistoryRepository.flush();
            log.debug("Se reportaron {} ordenes procesadas a {}", successfullyReported, appProperties.getCompServices().getUrl());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //Generar un endpoint para consultar reportes.
}
