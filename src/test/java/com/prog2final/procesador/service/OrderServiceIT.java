package com.prog2final.procesador.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.prog2final.procesador.IntegrationTest;
import com.prog2final.procesador.config.Constants;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import java.time.Instant;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class OrderServiceIT {

    public static final String MOCK_API_URL = "127.0.0.1:5000";
    public static final String GENERATOR_TEST_ORDERS_ENDPOINT = "/ordenes/ordenes/";
    public static final String COMP_SERVICES_TEST_CLIENTS_ENDPOINT = "/clientes/";
    public static final String COMP_SERVICES_TEST_STOCKS_ENDPOINT = "/acciones/";
    public static final String COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT = "/reporte-operaciones/consulta_cliente_accion";

    public static final JSONObject ORDER_JSON_1 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":127.78,\"cantidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"PRINCIPIODIA\"}," +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}," +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":118.23,\"cantidad\":10,\"fechaOperacion\":\"2023-11-30T18:00:00Z\",\"modo\":\"FINDIA\"}" +
        "]}"
    );

    public static final JSONObject ORDER_JSON_2 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"accionId\":33412413,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"accion\":\"PAM\",\"precio\":127.78,\"cliente\":51113,\"cantidad\":20,\"operacion\":\"COMPRA\",\"modo\":\"PRINCIPIODIA\"}," +
        "{\"cliente\":0,\"operacion\":\"COMPRA\",\"accionId\":13,\"accion\":\"PAM\",\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"precio\":119.17,\"modo\":\"AHORA\"}" +
        "]}"
    );

    public static final JSONObject ORDER_JSON_3 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":127.78,\"cantidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"SARASA\"}," +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VLLC\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}" +
        "]}"
    );

    public static final JSONObject ORDER_JSON_4 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"cliesnte\":51113,\"acciwdwonId\":13,\"accion\":\"PAM\",\"operwdwacion\":\"COMPRA\",\"precio\":127.78,\"cantedidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"SARASA\"}," +
        "{\"cliente\":51113,\"accdwionId\":13,\"accion\":\"PAM\",\"operacion\":\"VLLC\",\"precio\":119.17,\"cawdntidad\":32,\"fechaOperappcion\":\"2023-11-30T12:00:00Z\",\"modwdo\":\"AHORA\"}" +
        "]}"
    );

    public static final JSONObject ORDER_JSON_5 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"cliente\":\"a\",\"accionId\":13,\"accion\":12123,\"operacion\":45.34,\"precio\":\"g\",\"cantidad\":\"j\",\"fechaOperacion\":\"2023-13518:00:00\",\"modo\":1}" +
        "]}"
    );

    public static final JSONObject ORDER_JSON_6 = new JSONObject(
        "{\"ordenes\":[" +
        "{\"cliente\":213323,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}," +
        "{\"cliente\":51113,\"accionId\":1312121,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "]}"
    );
    public static final JSONObject CLIENT_JSON_1 = new JSONObject(
        "{\"clientes\":[{\"id\":51113,\"nombreApellido\":\"María Corvalán\",\"empresa\":\"Happy Soul\"}]}"
    );
    public static final JSONObject STOCKS_JSON_1 = new JSONObject(
        "{\"acciones\":[{\"id\":13,\"codigo\":\"PAM\",\"empresa\":\"Pampa Energia SA\"}]}"
    );
    public static final JSONObject CLIENT_STOCK_JSON_1 = new JSONObject(
        "{\"cliente\":51113,\"accionId\":351124,\"accion\":\"PAM\",\"cantidadActual\":100,\"observaciones\":\"Acciones presentes\"}"
    );
    public static final JSONObject CLIENT_STOCK_JSON_2 = new JSONObject(
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"---\",\"cantidadActual\":null,\"observaciones\":\"No existen acciones compradas para ese clienteId y accionId\"}"
    );

    private List<OrderHistory> orderHistoryList1;
    private List<OrderHistory> orderHistoryList2;
    private List<OrderHistory> orderHistoryList3;
    private List<OrderHistory> orderHistoryList4;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderService orderService;

    @BeforeClass
    void createOrderHistoryArray() {
        JSONArray[] auxArr = {
            ORDER_JSON_1.getJSONArray("ordenes"),
            ORDER_JSON_2.getJSONArray("ordenes"),
            ORDER_JSON_3.getJSONArray("ordenes"),
            ORDER_JSON_6.getJSONArray("ordenes"),
        };

        for (int i = 0; i < auxArr.length; i++) {
            JSONArray arr = auxArr[i];
            for (int j = 0; j < arr.length(); j++) {
                OrderHistory order = new OrderHistory()
                    .cliente(arr.getJSONObject(j).getLong("cliente"))
                    .accionId(arr.getJSONObject(j).getLong("accionId"))
                    .accion(arr.getJSONObject(j).getString("accion"))
                    .operacion(arr.getJSONObject(j).getString("operacion").equals("COMPRA"))
                    .precio(arr.getJSONObject(j).getDouble("precio"))
                    .cantidad(arr.getJSONObject(j).getDouble("cantidad"))
                    .fechaOperacion(Instant.parse(arr.getJSONObject(j).getString("fechaOperacion")))
                    .modo(Modo.valueOf(arr.getJSONObject(j).getString("modo")))
                    .estado(Estado.PENDIENTE)
                    .operacionObservaciones("Esperando procesamiento...");
                switch (i) {
                    case 1:
                        orderHistoryList1.add(order);
                    case 2:
                        orderHistoryList2.add(order);
                    case 3:
                        orderHistoryList3.add(order);
                    case 4:
                        orderHistoryList4.add(order);
                }
            }
        }
    }

    @Test
    void assertThatJSONOrdersAreCorrectlyRequested() {
        JSONObject ordersJSON1 = orderService.getJSONFromEndpoint(MOCK_API_URL, GENERATOR_TEST_ORDERS_ENDPOINT + "1");
        assertThat(ordersJSON1).isEqualTo(ORDER_JSON_1);
        JSONObject ordersJSON2 = orderService.getJSONFromEndpoint(MOCK_API_URL, GENERATOR_TEST_ORDERS_ENDPOINT + "2");
        assertThat(ordersJSON2).isEqualTo(ORDER_JSON_2);
        JSONObject ordersJSON3 = orderService.getJSONFromEndpoint(MOCK_API_URL, GENERATOR_TEST_ORDERS_ENDPOINT + "3");
        assertThat(ordersJSON3).isEqualTo(ORDER_JSON_3);
    }

    @Test
    void assertThatJSONOrdersAreCorrectlyPersisted1() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList1);
    }

    @Test
    @Transactional
    void assertThatJSONOrdersAreCorrectlyPersisted2() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_2);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(2);
        assertThat(requestedOrders).isEqualTo(orderHistoryList2);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList2);
    }

    @Test
    @Transactional
    void assertThatInvalidJSONsRaiseException1() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_3);
        assertThatThrownBy(() -> {
                orderService.getAndPersistNewOrders();
            })
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("No enum constant (.+)");
    }

    @Test
    void assertThatInvalidJSONsRaiseException2() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_4);
        assertThatThrownBy(() -> {
                orderService.getAndPersistNewOrders();
            })
            .isInstanceOf(RuntimeException.class)
            .hasMessageMatching("org\\\\.json\\\\.JSONException: JSONObject\\[\"([^\"]+)\"\\] not found");
    }

    @Test
    void assertThatInvalidJSONsRaiseException3() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_5);
        assertThatThrownBy(() -> {
                orderService.getAndPersistNewOrders();
            })
            .isInstanceOf(RuntimeException.class)
            .hasMessageMatching(
                "org\\\\.json\\\\.JSONException: JSONObject\\\\[\"([^\"]+)\"\\\\] is not a (\\\\S+) \\\\(class (.+?) : (.+?)\\\\)"
            );
    }

    @Test
    @Transactional
    void assertThatAValidOrderIsAccepted() {
        OrderHistory validOrder = orderHistoryList1.get(0);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        when(Instant.now().toString()).thenReturn("2023-11-30T12:00:00Z");
        List<Object> result = orderService.verifyIfValidOrder(validOrder);
        assertThat(result.get(0)).isEqualTo(true);
        assertThat(result.get(1)).isEqualTo("OK.");
    }

    @Test
    @Transactional
    void assertThatAnInvalidOrderIsRejected1() {
        OrderHistory invalidOrder = orderHistoryList1.get(0);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        when(Instant.now().toString()).thenReturn("2023-11-30T19:00:00Z");
        List<Object> result = orderService.verifyIfValidOrder(invalidOrder);
        assertThat(result.get(0)).isEqualTo(false);
        assertThat(result.get(1))
            .isEqualTo(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
    }

    @Test
    @Transactional
    void assertThatAnInvalidOrderIsRejected2() {
        OrderHistory invalidOrder1 = orderHistoryList2.get(0);
        OrderHistory invalidOrder2 = orderHistoryList2.get(1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        when(Instant.now().toString()).thenReturn("2023-11-30T12:00:00Z");
        List<Object> result1 = orderService.verifyIfValidOrder(invalidOrder1);
        List<Object> result2 = orderService.verifyIfValidOrder(invalidOrder2);
        assertThat(result1.get(0)).isEqualTo(false);
        assertThat(result1.get(1))
            .isEqualTo(
                "ID de cliente o código de acción inválido (uno o ambos de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(result2.get(0)).isEqualTo(false);
        assertThat(result2.get(1))
            .isEqualTo(
                "ID de cliente o código de acción inválido (uno o ambos de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
    }

    @Test
    @Transactional
    void assertThatAnInvalidOrderIsRejected3() {
        OrderHistory invalidOrder = orderHistoryList1.get(2);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_2);
        when(Instant.now().toString()).thenReturn("2023-11-30T12:00:00Z");
        List<Object> result = orderService.verifyIfValidOrder(invalidOrder);
        assertThat(result.get(0)).isEqualTo(false);
        assertThat(result.get(1))
            .isEqualTo(
                "No hay suficientes acciones para vender (el cliente solicitado no posee la cantidad de acciones necesaria para proceder con la operación)."
            );
    }

    @Test
    @Transactional
    void assertThatValidOrdersAreProcessedCorrectly() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList1);
        when(Instant.now().toString()).thenReturn("2023-11-30T12:00:00Z");
        List<OrderHistory> successfulOrders = orderService.performProcessing(requestedOrders);
        assertThat(successfulOrders).hasSize(4);
        for (OrderHistory ord : successfulOrders) {
            assertThat(ord.getEstado()).isEqualTo(Estado.EXITOSA);
            assertThat(ord.getOperacionObservaciones()).isEqualTo("OK.");
        }
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> failedOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.FALLIDA);
        assertThat(successfulOrdersInDb).hasSize(4);
        assertThat(failedOrdersInDb).hasSize(0);
    }

    @Test
    @Transactional
    void assertThatInvalidOrdersAreProcessedCorrectly() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_6);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(3);
        assertThat(requestedOrders).isEqualTo(orderHistoryList4);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList4);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_2);
        when(Instant.now().toString()).thenReturn("2023-11-30T12:00:00Z");
        List<OrderHistory> successfulOrders = orderService.performProcessing(requestedOrders);
        assertThat(successfulOrders).hasSize(0);
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> failedOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.FALLIDA);
        assertThat(successfulOrdersInDb).hasSize(0);
        assertThat(failedOrdersInDb).hasSize(3);
        assertThat(failedOrdersInDb.get(0).getOperacionObservaciones())
            .isEqualTo(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
        assertThat(failedOrdersInDb.get(1).getOperacionObservaciones())
            .isEqualTo(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
        assertThat(failedOrdersInDb.get(2).getOperacionObservaciones())
            .isEqualTo(
                "ID de cliente o código de acción inválido (uno o ambos de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
    }

    @Test
    @Transactional
    void assertThatPrincipioDiaOrdersAreProcessedCorrectly() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        List<OrderHistory> successfulOrders = orderService.processStartOfDayOrders();
        assertThat(successfulOrders).hasSize(1);
        assertThat(successfulOrders.get(0).getEstado()).isEqualTo(Estado.EXITOSA);
        assertThat(successfulOrders.get(0).getOperacionObservaciones()).isEqualTo("OK.");
        assertThat(successfulOrders.get(0).getModo()).isEqualTo(Modo.PRINCIPIODIA);
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> pendingOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.PENDIENTE);
        assertThat(successfulOrdersInDb).hasSize(1);
        assertThat(pendingOrdersInDb).hasSize(3);
        assertThat(successfulOrdersInDb.get(0).getOperacionObservaciones()).isEqualTo("OK.");
        assertThat(successfulOrdersInDb.get(0).getModo()).isEqualTo(Modo.PRINCIPIODIA);
    }

    @Test
    @Transactional
    void assertThatAhoraOrdersAreProcessedCorrectly() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        List<OrderHistory> successfulOrders = orderService.processInstantOrders();
        assertThat(successfulOrders).hasSize(2);
        for (OrderHistory ord : successfulOrders) {
            assertThat(ord.getEstado()).isEqualTo(Estado.EXITOSA);
            assertThat(ord.getOperacionObservaciones()).isEqualTo("OK.");
            assertThat(ord.getModo()).isEqualTo(Modo.AHORA);
        }
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> pendingOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.PENDIENTE);
        assertThat(successfulOrdersInDb).hasSize(2);
        assertThat(pendingOrdersInDb).hasSize(2);
        for (OrderHistory ord : successfulOrdersInDb) {
            assertThat(ord.getEstado()).isEqualTo(Estado.EXITOSA);
            assertThat(ord.getOperacionObservaciones()).isEqualTo("OK.");
            assertThat(ord.getModo()).isEqualTo(Modo.AHORA);
        }
    }

    @Test
    @Transactional
    void assertThatFinDiaOrdersAreProcessedCorrectly() {
        when(orderService.getJSONFromEndpoint(anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).isEqualTo(orderHistoryList1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENTS_ENDPOINT)).thenReturn(CLIENT_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_STOCKS_ENDPOINT)).thenReturn(STOCKS_JSON_1);
        when(orderService.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT + anyString()))
            .thenReturn(CLIENT_STOCK_JSON_1);
        List<OrderHistory> successfulOrders = orderService.processEndOfDayOrders();
        assertThat(successfulOrders).hasSize(1);
        assertThat(successfulOrders.get(0).getEstado()).isEqualTo(Estado.EXITOSA);
        assertThat(successfulOrders.get(0).getOperacionObservaciones()).isEqualTo("OK.");
        assertThat(successfulOrders.get(0).getModo()).isEqualTo(Modo.FINDIA);
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> pendingOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.PENDIENTE);
        assertThat(successfulOrdersInDb).hasSize(1);
        assertThat(pendingOrdersInDb).hasSize(3);
        assertThat(successfulOrdersInDb.get(0).getOperacionObservaciones()).isEqualTo("OK.");
        assertThat(successfulOrdersInDb.get(0).getModo()).isEqualTo(Modo.FINDIA);
    }

    @AfterAll
    void removeEntitiesFromDatabase() {
        orderHistoryRepository.deleteAll();
    }
}
