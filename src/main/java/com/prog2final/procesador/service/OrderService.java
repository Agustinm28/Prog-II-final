package com.prog2final.procesador.service;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.prog2final.procesador.config.Constants;
import com.prog2final.procesador.domain.ClientStocks;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Language;
import com.prog2final.procesador.repository.ClientStocksRepository;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public static final String GENERATOR_ORDERS_ENDPOINT = "/ordenes/ordenes/";
    public static final String COMP_SERVICES_STOCKS_ENDPOINT = "/acciones/";
    public static final String COMP_SERVICES_CLIENTS_ENDPOINT = "/clientes/";
    public static final String COMP_SERVICES_REPORTS_ENDPOINT = "/reporte-operaciones/reportar/";

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderHistoryRepository orderHistoryRepository;

    private final ClientStocksRepository clientStocksRepository;

    public OrderService(OrderHistoryRepository orderHistoryRepository, ClientStocksRepository clientStocksRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.clientStocksRepository = clientStocksRepository;
    }

    @Transactional
    public JSONArray getJSONArrayFromEndpoint(String baseUrl, String endpointSuffix) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
            .newBuilder(URI.create(baseUrl + endpointSuffix))
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + Constants.CATEDRA_TOKEN)
            .timeout(Duration.of(10, SECONDS))
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray responseJson = new JSONArray(response.body());
            return responseJson;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(initialDelay = 20, fixedRate = 1000)
    @Transactional
    public List<OrderHistory> getAndPersistNewOrders() {
        JSONArray ordersJSON = getJSONArrayFromEndpoint(Constants.GENERATOR_URL, GENERATOR_ORDERS_ENDPOINT);
        ArrayList<OrderHistory> processedOrders = new ArrayList<>();

        for (int i = 0; i < ordersJSON.length(); i++) {
            OrderHistory orderEntity = new OrderHistory();
            try {
                JSONObject orderJson = ordersJSON.getJSONObject(i);
                orderEntity
                    .clientId(orderJson.getLong("cliente"))
                    .stockCode(orderJson.getString("accion"))
                    .operationType(orderJson.getString("operacion").equals("COMPRA") ? true : false)
                    .price(orderJson.getDouble("precio"))
                    .amount(orderJson.getDouble("cantidad"))
                    .creationDate(Instant.parse(orderJson.getString("fechaOperacion")))
                    .mode(orderJson.getString("modo"))
                    .state("PENDING")
                    .language(Language.SPANISH)
                    .info("Awating processing...");
                orderHistoryRepository.save(orderEntity);
                processedOrders.add(orderEntity);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        log.debug("Attempted to get new orders: got and persisted {} orders from order generator.", processedOrders.size());
        return processedOrders;
    }

    /* @Transactional
    public void deleteOrdersAtGenerator() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(Constants.COMP_SERVICES_URL + GENERATOR_ORDERS_ENDPOINT))
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + Constants.CATEDRA_TOKEN)
            .timeout(Duration.of(10, SECONDS))
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray responseJson = new JSONArray(response.body());
            return responseJson;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    } */

    @Transactional
    public List<OrderHistory> performProcessing(List<OrderHistory> orders) {
        List<OrderHistory> successfulOrders = new ArrayList<>();
        for (OrderHistory order : orders) {
            List<Object> processingResult = verifyIfValidOrder(order);
            if (processingResult.get(0).equals(false)) {
                order.state("FAILED").info(processingResult.get(1).toString()).executionDate(Instant.now());
            } else {
                Optional<ClientStocks> clientStockFromRepo = clientStocksRepository.findOneByClientIdAndStockCodeAndStockAmountGreaterThanEqual(
                    order.getClientId(),
                    order.getStockCode(),
                    0D
                );
                ClientStocks clientStockEntity = clientStockFromRepo.isPresent()
                    ? clientStockFromRepo.get()
                    : new ClientStocks().clientId(order.getClientId()).stockCode(order.getStockCode()).stockAmount(0D);
                if (order.getOperationType()) {
                    clientStockEntity.setStockAmount(clientStockEntity.getStockAmount() + order.getAmount());
                } else {
                    clientStockEntity.setStockAmount(clientStockEntity.getStockAmount() - order.getAmount());
                }
                clientStocksRepository.save(clientStockEntity);
                order.state("SUCCEEDED").info(processingResult.get(1).toString()).executionDate(Instant.now());
            }
            orderHistoryRepository.save(order);
        }
        return successfulOrders;
    }

    @Scheduled(cron = "0 0 9 * * ?", zone = "Etc/UTC")
    @Transactional
    public List<OrderHistory> processStartOfDayOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModeAndState("PRINCIPIODIA", "PENDING");
        List<OrderHistory> succesfulOrders = performProcessing(orders);
        log.debug(
            "Processing of 'PRINCIPIODIA' orders: {} successful orders, {} failed orders.",
            succesfulOrders.size(),
            orders.size() - succesfulOrders.size()
        );
        return succesfulOrders;
    }

    @Scheduled(cron = "0 59 17 * * ?", zone = "Etc/UTC")
    @Transactional
    public List<OrderHistory> processEndOfDayOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModeAndState("FINDIA", "PENDING");
        List<OrderHistory> succesfulOrders = performProcessing(orders);
        log.debug(
            "Processing of 'FINDIA' orders: {} successful orders, {} failed orders.",
            succesfulOrders.size(),
            orders.size() - succesfulOrders.size()
        );
        return succesfulOrders;
    }

    @Scheduled(initialDelay = 50, fixedRate = 1000)
    @Transactional
    public List<OrderHistory> processInstantOrders() {
        List<OrderHistory> orders = orderHistoryRepository.findAllByModeAndState("AHORA", "PENDING");
        List<OrderHistory> succesfulOrders = performProcessing(orders);
        log.debug(
            "Processing of 'AHORA' orders: {} successful orders, {} failed orders.",
            succesfulOrders.size(),
            orders.size() - succesfulOrders.size()
        );
        return succesfulOrders;
    }

    @Transactional
    public List<Object> verifyIfValidOrder(OrderHistory order) {
        String currentTime = Instant.now().toString().split("T")[1].replaceFirst("Z", "");
        ArrayList<Object> result = new ArrayList<>();
        if (currentTime.compareTo("09:00:00") < 0 || currentTime.compareTo("18:00:00") > 0) {
            result.add(false);
            result.add("Out of processing range (an attempt was made to process the order before 09:00:00 or after 18:00:00 UTC).");
            return result;
        }

        JSONArray clients = getJSONArrayFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_CLIENTS_ENDPOINT);
        HashSet<Integer> uniqueIds = new HashSet<>();

        for (int i = 0; i < clients.length(); i++) {
            try {
                JSONObject clientJson = clients.getJSONObject(i);
                uniqueIds.add(clientJson.getInt("cliente"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        JSONArray stocks = getJSONArrayFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_STOCKS_ENDPOINT);
        HashSet<String> uniqueCodes = new HashSet<>();

        for (int i = 0; i < stocks.length(); i++) {
            try {
                JSONObject clientJson = clients.getJSONObject(i);
                uniqueCodes.add(clientJson.getString("codigo"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (!(uniqueIds.contains(order.getClientId()) && uniqueCodes.contains(order.getStockCode()))) {
            result.add(false);
            result.add(
                "Invalid client ID and/or stock code (one or both of the specified identifiers did not match an existing client/stock)."
            );
            return result;
        }

        if (
            order.getOperationType() &&
            !clientStocksRepository
                .findOneByClientIdAndStockCodeAndStockAmountGreaterThanEqual(order.getClientId(), order.getStockCode(), order.getAmount())
                .isPresent()
        ) {
            result.add(false);
            result.add(
                "Not enough stocks available for selling (the specified client did not have sufficient stocks to perform the operation)."
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
        List<OrderHistory> ordersToReport = orderHistoryRepository.findAllByState("SUCCEEDED");
        ordersToReport.addAll(orderHistoryRepository.findAllByState("FAILED"));

        JSONArray ordersToReportJSON = new JSONArray(ordersToReport);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
            .newBuilder(URI.create(Constants.COMP_SERVICES_URL + COMP_SERVICES_REPORTS_ENDPOINT))
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + Constants.CATEDRA_TOKEN)
            .timeout(Duration.of(10, SECONDS))
            .POST(HttpRequest.BodyPublishers.ofString(ordersToReportJSON.toString()))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                for (OrderHistory order : ordersToReport) {
                    order.state("REPORTED");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Scheduled(initialDelay = 1000, fixedRate = 30000)
    public void removeAlreadyReportedOrders() {
        orderHistoryRepository.deleteAllByState("REPORTED");
    }
}
