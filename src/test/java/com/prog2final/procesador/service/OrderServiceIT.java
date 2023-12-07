package com.prog2final.procesador.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.prog2final.procesador.IntegrationTest;
import com.prog2final.procesador.config.Constants;
import com.prog2final.procesador.domain.OrderHistory;
import com.prog2final.procesador.domain.enumeration.Estado;
import com.prog2final.procesador.domain.enumeration.Modo;
import com.prog2final.procesador.repository.OrderHistoryRepository;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.internal.matchers.StartsWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class OrderServiceIT {

    public static final String COMP_SERVICES_TEST_CLIENTS_ENDPOINT = "/clientes/";
    public static final String COMP_SERVICES_TEST_STOCKS_ENDPOINT = "/acciones/";
    public static final String COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT = "/reporte-operaciones/consulta_cliente_accion";
    public static final String COMP_SERVICES_TEST_ORDER_HISTORY_ENDPOINT = "/reporte-operaciones/consulta";

    public static final String ORDER_JSON_1 =
        "{\"ordenes\":[" +
        "{\"id\":1,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":127.78,\"cantidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"PRINCIPIODIA\"}," +
        "{\"id\":2,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":3,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":4,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":118.23,\"cantidad\":10,\"fechaOperacion\":\"2023-11-30T18:00:00Z\",\"modo\":\"FINDIA\"}" +
        "]}";

    public static final String ORDER_JSON_2 =
        "{\"ordenes\":[" +
        "{\"accionId\":232314,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"accion\":\"PAM\",\"precio\":127.78,\"id\":1,\"cliente\":51113,\"cantidad\":20,\"operacion\":\"COMPRA\",\"modo\":\"PRINCIPIODIA\"}," +
        "{\"cliente\":12321312,\"operacion\":\"COMPRA\",\"accionId\":13,\"accion\":\"PAM\",\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"precio\":119.17,\"modo\":\"AHORA\",\"id\":2}," +
        "{\"cliente\":51113,\"id\":3,\"operacion\":\"VENTA\",\"accionId\":13,\"accion\":\"TGTGTGTG\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}" +
        "]}";

    public static final String ORDER_JSON_3 =
        "{\"ordenes\":[" +
        "{\"id\":1,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":127.78,\"cantidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"SARASA\"}," +
        "{\"id\":2,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VLLC\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}" +
        "]}";

    public static final String ORDER_JSON_4 =
        "{\"ordenes\":[" +
        "{\"id\":1,\"cliesnte\":51113,\"acciwdwonId\":13,\"accion\":\"PAM\",\"operwdwacion\":\"COMPRA\",\"precio\":127.78,\"cantedidad\":20,\"fechaOperacion\":\"2023-11-30T04:00:00Z\",\"modo\":\"SARASA\"}," +
        "{\"id\":2,\"cliente\":51113,\"accdwionId\":13,\"accion\":\"PAM\",\"operacion\":\"VLLC\",\"precio\":119.17,\"cawdntidad\":32,\"fechaOperappcion\":\"2023-11-30T12:00:00Z\",\"modwdo\":\"AHORA\"}" +
        "]}";

    public static final String ORDER_JSON_5 =
        "{\"ordenes\":[" +
        "{\"id\":\"frf\",\"cliente\":\"a\",\"accionId\":13,\"accion\":12123,\"operacion\":45.34,\"precio\":\"g\",\"cantidad\":\"j\",\"fechaOperacion\":\"2023-13518:00:00\",\"modo\":1}" +
        "]}";

    public static final String ORDER_JSON_6 =
        "{\"ordenes\":[" +
        "{\"id\":1,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":2,\"cliente\":213323,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"COMPRA\",\"precio\":119.17,\"cantidad\":32,\"fechaOperacion\":\"2023-11-30T12:00:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":3,\"cliente\":51113,\"accionId\":1312121,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":4,\"cliente\":51113,\"accionId\":13,\"accion\":\"SDSDSD\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "{\"id\":5,\"cliente\":51113,\"accionId\":13,\"accion\":\"PAM\",\"operacion\":\"VENTA\",\"precio\":122.96,\"cantidad\":12,\"fechaOperacion\":\"2023-11-30T14:20:00Z\",\"modo\":\"AHORA\"}," +
        "]}";
    public static final String CLIENT_JSON_1 =
        "{\"clientes\":[{\"id\":51113,\"nombreApellido\":\"María Corvalán\",\"empresa\":\"Happy Soul\"}]}";
    public static final String STOCKS_JSON_1 = "{\"acciones\":[{\"id\":13,\"codigo\":\"PAM\",\"empresa\":\"Pampa Energia SA\"}]}";
    public static final String CLIENT_STOCK_JSON_1 =
        "{\"cliente\":51113,\"accionId\":351124,\"accion\":\"PAM\",\"cantidadActual\":100,\"observaciones\":\"Acciones presentes\"}";
    public static final String CLIENT_STOCK_JSON_2 =
        "{\"cliente\":51113,\"accionId\":13,\"accion\":\"---\",\"cantidadActual\":null,\"observaciones\":\"No existen acciones compradas para ese clienteId y accionId\"}";

    private final String GENERATOR_TOKEN = Dotenv.load().get("GENERATOR_TOKEN");
    private final String COMP_SERVICES_TOKEN = Dotenv.load().get("COMP_SERVICES_TOKEN");

    private List<OrderHistory> orderHistoryList1;
    private List<OrderHistory> orderHistoryList2;
    private List<OrderHistory> orderHistoryList3;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private JSONRequester realRequester;

    @Mock
    private JSONRequester mockRequester;

    @BeforeEach
    public void initialize() {
        orderService.setJsonRequester(mockRequester);

        orderHistoryList1 = new ArrayList<>();
        orderHistoryList2 = new ArrayList<>();
        orderHistoryList3 = new ArrayList<>();

        JSONArray[] auxArr = {
            new JSONObject(ORDER_JSON_1).getJSONArray("ordenes"),
            new JSONObject(ORDER_JSON_2).getJSONArray("ordenes"),
            new JSONObject(ORDER_JSON_6).getJSONArray("ordenes"),
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
                    case 0:
                        orderHistoryList1.add(order);
                        break;
                    case 1:
                        orderHistoryList2.add(order);
                        break;
                    case 2:
                        orderHistoryList3.add(order);
                        break;
                }
            }
        }
    }

    @Test
    void assertThatJSONOrdersAreCorrectlyPersisted1() {
        when(mockRequester.getJSONFromEndpoint(anyString(), anyString(), anyString())).thenReturn(ORDER_JSON_1);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(4);
        assertThat(requestedOrders).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
    }

    @Test
    @Transactional
    void assertThatJSONOrdersAreCorrectlyPersisted2() {
        when(mockRequester.getJSONFromEndpoint(anyString(), anyString(), anyString())).thenReturn(ORDER_JSON_2);
        List<OrderHistory> requestedOrders = orderService.getAndPersistNewOrders();
        assertThat(requestedOrders).hasSize(3);
        assertThat(requestedOrders).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList2);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList2);
    }

    @Test
    @Transactional
    void assertThatInvalidJSONsRaiseException1() {
        when(mockRequester.getJSONFromEndpoint(anyString(), anyString(), anyString())).thenReturn(ORDER_JSON_3);
        assertThatThrownBy(orderService::getAndPersistNewOrders)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageMatching("No enum constant (.+)");
    }

    @Test
    @Transactional
    void assertThatInvalidJSONsRaiseException2() {
        when(mockRequester.getJSONFromEndpoint(anyString(), anyString(), anyString())).thenReturn(ORDER_JSON_4);
        assertThatThrownBy(orderService::getAndPersistNewOrders)
            .isInstanceOf(RuntimeException.class)
            .hasMessageMatching("org\\.json\\.JSONException: JSONObject\\[\"[a-zA-Z0-9]*\"] not found\\.");
    }

    @Test
    @Transactional
    void assertThatInvalidJSONsRaiseException3() {
        when(mockRequester.getJSONFromEndpoint(anyString(), anyString(), anyString())).thenReturn(ORDER_JSON_5);
        assertThatThrownBy(orderService::getAndPersistNewOrders)
            .isInstanceOf(RuntimeException.class)
            .hasMessageMatching("org\\.json\\.JSONException: JSONObject\\[\"[a-zA-Z0-9]*\"] is not a [a-zA-Z0-9]* \\(class .*\\)\\.$");
    }

    @Test
    @Transactional
    void assertThatAValidOrderIsAccepted() {
        OrderHistory validOrder = orderHistoryList1.get(0);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
        List<Object> result = orderService.verifyIfValidOrder(validOrder);
        assertThat(result.get(0)).isEqualTo(true);
        assertThat(result.get(1)).isEqualTo("OK.");
    }

    @Test
    @Transactional
    void assertThatAnInvalidOrderIsRejected1() {
        OrderHistory invalidOrder = orderHistoryList1.get(0);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T19:00:00Z"), ZoneId.of("Etc/UTC")));
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
        OrderHistory invalidOrder3 = orderHistoryList2.get(2);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        List<Object> result1 = orderService.verifyIfValidOrder(invalidOrder1);
        List<Object> result2 = orderService.verifyIfValidOrder(invalidOrder2);
        List<Object> result3 = orderService.verifyIfValidOrder(invalidOrder3);
        assertThat(result1.get(0)).isEqualTo(false);
        assertThat(result1.get(1))
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(result2.get(0)).isEqualTo(false);
        assertThat(result2.get(1))
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(result3.get(0)).isEqualTo(false);
        assertThat(result3.get(1))
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
    }

    @Test
    @Transactional
    void assertThatAnInvalidOrderIsRejected3() {
        OrderHistory invalidOrder = orderHistoryList1.get(2);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_2);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
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
        orderHistoryRepository.saveAllAndFlush(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
        List<OrderHistory> successfulOrders = orderService.performProcessing(ordersInDb);
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
        orderHistoryRepository.saveAllAndFlush(orderHistoryList3);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList3);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_2);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T03:00:00Z"), ZoneId.of("Etc/UTC")));
        List<OrderHistory> successfulOrders = orderService.performProcessing(List.of(ordersInDb.get(0)));
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
        ordersInDb.remove(0);
        successfulOrders.addAll(orderService.performProcessing(ordersInDb));
        assertThat(successfulOrders).hasSize(0);
        List<OrderHistory> successfulOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.EXITOSA);
        List<OrderHistory> failedOrdersInDb = orderHistoryRepository.findAllByEstado(Estado.FALLIDA);
        assertThat(successfulOrdersInDb).hasSize(0);
        assertThat(failedOrdersInDb).hasSize(5);
        assertThat(failedOrdersInDb.get(0).getOperacionObservaciones())
            .isEqualTo(
                "Fuera del rango de procesamiento (se intentó procesar la orden antes de las 09:00:00 or después de las 18:00:00 UTC)."
            );
        assertThat(failedOrdersInDb.get(1).getOperacionObservaciones())
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(failedOrdersInDb.get(2).getOperacionObservaciones())
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(failedOrdersInDb.get(3).getOperacionObservaciones())
            .isEqualTo(
                "ID de cliente o ID/código de acción inválido (uno o varios de los identificadores especificados no corresponde a un cliente/acción válida)."
            );
        assertThat(failedOrdersInDb.get(4).getOperacionObservaciones())
            .isEqualTo(
                "No hay suficientes acciones para vender (el cliente solicitado no posee la cantidad de acciones necesaria para proceder con la operación)."
            );
    }

    @Test
    @Transactional
    void assertThatPrincipioDiaOrdersAreProcessedCorrectly() {
        orderHistoryRepository.saveAllAndFlush(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
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
        orderHistoryRepository.saveAllAndFlush(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
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
        orderHistoryRepository.saveAllAndFlush(orderHistoryList1);
        List<OrderHistory> ordersInDb = orderHistoryRepository.findAll();
        assertThat(ordersInDb).usingRecursiveComparison().ignoringFields("id").isEqualTo(orderHistoryList1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_CLIENTS_ENDPOINT), anyString()))
            .thenReturn(CLIENT_JSON_1);
        when(mockRequester.getJSONFromEndpoint(eq(Constants.COMP_SERVICES_URL), eq(COMP_SERVICES_TEST_STOCKS_ENDPOINT), anyString()))
            .thenReturn(STOCKS_JSON_1);
        when(
            mockRequester.getJSONFromEndpoint(
                eq(Constants.COMP_SERVICES_URL),
                argThat(new StartsWith(COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT)),
                anyString()
            )
        )
            .thenReturn(CLIENT_STOCK_JSON_1);
        orderService.setInternalClock(Clock.fixed(Instant.parse("2023-11-30T12:00:00Z"), ZoneId.of("Etc/UTC")));
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

    @Test
    @Transactional
    void assertThatOrdersAreProperlyReported() {
        OrderHistory ord1 = orderHistoryList1.get(0).estado(Estado.EXITOSA).fechaEjecucion(Instant.now());
        OrderHistory ord2 = orderHistoryList1.get(1).estado(Estado.FALLIDA).fechaEjecucion(Instant.now());
        OrderHistory ord3 = orderHistoryList1.get(2).estado(Estado.REPORTADA).fechaEjecucion(Instant.now());
        List<OrderHistory> modifiedOrders = new ArrayList<>();
        Collections.addAll(modifiedOrders, ord1, ord2, ord3, orderHistoryList1.get(3));
        orderHistoryRepository.saveAllAndFlush(modifiedOrders);
        assertThat(orderHistoryRepository.findAll()).hasSize(4);
        assertThat(orderHistoryRepository.findAllByEstado(Estado.REPORTADA)).hasSize(1);

        JSONObject clientStockJSON = new JSONObject(
            realRequester.getJSONFromEndpoint(
                Constants.COMP_SERVICES_URL,
                COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT +
                String.format("?clienteId=%s&accionId=%s", ord1.getCliente(), ord1.getAccionId()),
                COMP_SERVICES_TOKEN
            )
        );
        Double previousStockAmount = clientStockJSON.isNull("cantidadActual") ? 0D : clientStockJSON.getDouble("cantidadActual");
        JSONArray orderHistoryArray = new JSONArray(
            realRequester.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_ORDER_HISTORY_ENDPOINT, COMP_SERVICES_TOKEN)
        );
        orderService.reportOrders();

        assertThat(orderHistoryRepository.findAllByEstado(Estado.REPORTADA)).hasSize(3);
        assertThat(orderHistoryRepository.findAllByEstado(Estado.PENDIENTE)).hasSize(1);

        JSONObject newClientStockJSON = new JSONObject(
            realRequester.getJSONFromEndpoint(
                Constants.COMP_SERVICES_URL,
                COMP_SERVICES_TEST_CLIENT_STOCK_ENDPOINT +
                String.format("?clienteId=%s&accionId=%s", ord1.getCliente(), ord1.getAccionId()),
                COMP_SERVICES_TOKEN
            )
        );
        Double newStockAmount = newClientStockJSON.isNull("cantidadActual") ? 0D : newClientStockJSON.getDouble("cantidadActual");
        JSONArray newOrderHistoryArray = new JSONArray(
            realRequester.getJSONFromEndpoint(Constants.COMP_SERVICES_URL, COMP_SERVICES_TEST_ORDER_HISTORY_ENDPOINT, COMP_SERVICES_TOKEN)
        );

        assertThat(newStockAmount).isEqualTo(previousStockAmount + ord1.getCantidad());
        assertThat(newOrderHistoryArray).hasSize(orderHistoryArray.length() + 2);
    }

    @Test
    @Transactional
    void assertThatReportedOrdersAreRemoved() {
        OrderHistory ord1 = orderHistoryList1.get(0).estado(Estado.EXITOSA).fechaEjecucion(Instant.now());
        OrderHistory ord2 = orderHistoryList1.get(1).estado(Estado.FALLIDA).fechaEjecucion(Instant.now());
        OrderHistory ord3 = orderHistoryList1.get(2).estado(Estado.REPORTADA).fechaEjecucion(Instant.now());
        List<OrderHistory> modifiedOrders = new ArrayList<>();
        Collections.addAll(modifiedOrders, ord1, ord2, ord3, orderHistoryList1.get(3));
        orderHistoryRepository.saveAllAndFlush(modifiedOrders);
        assertThat(orderHistoryRepository.findAll()).hasSize(4);
        assertThat(orderHistoryRepository.findAllByEstado(Estado.REPORTADA)).hasSize(1);
        orderService.removeAlreadyReportedOrders();
        assertThat(orderHistoryRepository.findAll()).hasSize(3);
        assertThat(orderHistoryRepository.findAllByEstado(Estado.REPORTADA)).hasSize(0);
    }

    @AfterEach
    void removeEntitiesFromDatabase() {
        orderHistoryRepository.deleteAll();
        orderHistoryRepository.flush();
    }
}
