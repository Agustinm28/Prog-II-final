package com.prog2final.procesador.service;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.prog2final.procesador.config.Constants;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    public static final String GENERATOR_ORDERS_ENDPOINT = "/ordenes/ordenes/";
    public static final String COMP_SERVICES_STOCKS_ENDPOINT = "/acciones/";
    public static final String COMP_SERVICES_CLIENTS_ENDPOINT = "/clientes/";
    public static final String COMP_SERVICES_REPORTS_ENDPOINT = "/reporte-operaciones/reportar/";
    public static final String COMP_SERVICES_CLIENT_STOCK_ENDPOINT = "/reporte-operaciones/consulta_cliente_accion";

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

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
        JSONArray ordersJSON = new JSONObject(
            jsonRequester.getJSONFromEndpoint(Constants.GENERATOR_URL, GENERATOR_ORDERS_ENDPOINT, Constants.CATEDRA_TOKEN)
        )
            .getJSONArray("ordenes");
        ArrayList<OrderHistory> requestedOrders = new ArrayList<>();

        for (int i = 0; i < ordersJSON.length(); i++) {
            OrderHistory orderEntity = new OrderHistory();
            try {
                JSONObject orderJson = ordersJSON.getJSONObject(i);
                orderEntity
                    .cliente(orderJson.getLong("cliente"))
                    .accionId(orderJson.getLong("accionId"))
                    .accion(orderJson.getString("accion"))
                    .operacion(orderJson.getString("operacion").equals("COMPRA"))
                    .precio(orderJson.getDouble("precio"))
                    .cantidad(orderJson.getDouble("cantidad"))
                    .fechaOperacion(Instant.parse(orderJson.getString("fechaOperacion")))
                    .modo(Modo.valueOf(orderJson.getString("modo")))
                    .estado(Estado.PENDIENTE)
                    .operacionObservaciones("Esperando procesamiento...");
                orderHistoryRepository.save(orderEntity);
                requestedOrders.add(orderEntity);
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
            List<Object> processingResult = verifyIfValidOrder(order);
            if (processingResult.get(0).equals(false)) {
                order.estado(Estado.FALLIDA).fechaEjecucion(internalClock.instant());
            } else {
                order.estado(Estado.EXITOSA).fechaEjecucion(internalClock.instant());
                successfulOrders.add(order);
            }
            order.operacionObservaciones(processingResult.get(1).toString());
            orderHistoryRepository.save(order);
        }
        return successfulOrders;
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Etc/UTC")
    @Transactional
    public List<OrderHistory> processStartOfDayOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacion(Modo.PRINCIPIODIA, Estado.PENDIENTE);
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
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacion(Modo.FINDIA, Estado.PENDIENTE);
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
        List<OrderHistory> orders = orderHistoryRepository.findAllByModoAndEstadoOrderByFechaOperacion(Modo.AHORA, Estado.PENDIENTE);
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
    public List<Object> verifyIfValidOrder(OrderHistory order) {
        String currentTime = internalClock.instant().toString().split("T")[1].replaceFirst("Z", "");
        ArrayList<Object> result = new ArrayList<>();
        if (currentTime.compareTo("09:00:00") < 0 || currentTime.compareTo("18:00:00") > 0) {
            result.add(false);
            result.add(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
            return result;
        }

        JSONArray clients = new JSONObject(
            jsonRequester.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_CLIENTS_ENDPOINT, Constants.CATEDRA_TOKEN)
        )
            .getJSONArray("clientes");
        HashSet<Long> uniqueClientIds = new HashSet<>();

        for (int i = 0; i < clients.length(); i++) {
            try {
                JSONObject clientJson = clients.getJSONObject(i);
                uniqueClientIds.add(clientJson.getLong("id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        JSONArray stocks = new JSONObject(
            jsonRequester.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_STOCKS_ENDPOINT, Constants.CATEDRA_TOKEN)
        )
            .getJSONArray("acciones");
        HashSet<Long> uniqueStockIds = new HashSet<>();
        HashSet<String> uniqueStockCodes = new HashSet<>();

        for (int i = 0; i < stocks.length(); i++) {
            try {
                JSONObject stockJson = stocks.getJSONObject(i);
                uniqueStockIds.add(stockJson.getLong("id"));
                uniqueStockCodes.add(stockJson.getString("codigo"));
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

        JSONObject clientStockJSON = new JSONObject(
            jsonRequester.getJSONFromEndpoint(
                Constants.COMP_SERVICES_URL,
                COMP_SERVICES_CLIENT_STOCK_ENDPOINT + String.format("?clienteId=%s&accionId=%s", order.getCliente(), order.getAccionId()),
                Constants.CATEDRA_TOKEN
            )
        );
        Double stockAmount = clientStockJSON.isNull("cantidadActual") ? 0D : clientStockJSON.getDouble("cantidadActual");

        if (!order.getOperacion() && stockAmount < order.getCantidad()) {
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
        List<OrderHistory> successfulOrders = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> failedOrders = orderHistoryRepository.findAllByEstado(Estado.FALLIDA);

        JSONArray successfulJSONArr = new JSONArray(successfulOrders);
        JSONArray failedJSONArr = new JSONArray(failedOrders);

        JSONArray[] auxJSONArray = { successfulJSONArr, failedJSONArr };
        JSONArray ordersToReportJSONArr = new JSONArray();

        for (JSONArray a : auxJSONArray) {
            for (int i = 0; i < a.length(); i++) {
                JSONObject obj = a.getJSONObject(i);
                if (obj.getBoolean("operacion")) {
                    obj.put("operacion", "COMPRA");
                } else {
                    obj.put("operacion", "VENTA");
                }
                obj.put("operacionExitosa", obj.getEnum(Estado.class, "estado").equals(Estado.EXITOSA));
                ordersToReportJSONArr.put(obj);
            }
        }

        JSONObject ordersToReportJSON = new JSONObject().put("ordenes", ordersToReportJSONArr);
        System.out.println(ordersToReportJSON);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
            .newBuilder(URI.create(Constants.COMP_SERVICES_URL + COMP_SERVICES_REPORTS_ENDPOINT))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + Constants.CATEDRA_TOKEN)
            .timeout(Duration.of(10, SECONDS))
            .POST(HttpRequest.BodyPublishers.ofString(ordersToReportJSON.toString()))
            .build();

        int successfullyReported = 0;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<OrderHistory> allOrders = new ArrayList<>(successfulOrders);
                allOrders.addAll(failedOrders);
                for (OrderHistory order : allOrders) {
                    order.estado(Estado.REPORTADA);
                    order.operacionObservaciones("Orden reportada con éxito.");
                    successfullyReported++;
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.debug("Se reportaron {} ordenes procesadas a {}", successfullyReported, Constants.COMP_SERVICES_URL);
    }

    @Transactional
    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    public void removeAlreadyReportedOrders() {
        orderHistoryRepository.deleteAllByEstado(Estado.REPORTADA);
        log.debug("Todos los registros de órdenes reportadas han sido eliminados exitosamente.");
    }
}
