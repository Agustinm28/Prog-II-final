package com.progii.finalogen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Modo;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.web.rest.errors.BadRequestAlertException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class ReviewOrderServiceTest {

    @Mock
    private DataServices dataServices;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AditionalOrderServices aditionalOrderServices;

    @InjectMocks
    private FileLoader fileLoader;

    @InjectMocks
    private ReviewOrderService reviewOrderService;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        try {
            List<Map<String, Object>> clients = fileLoader.loadJson(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestClients.json"
            );
            List<Map<String, Object>> shares = fileLoader.loadJson(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestShares.json"
            );

            when(aditionalOrderServices.clientExists(1)).thenReturn(clients.get(0));
            when(aditionalOrderServices.accionExists(3)).thenReturn(shares.get(2));
            when(aditionalOrderServices.sellClientExists(1, 3)).thenReturn(40);
            when(dataServices.getLastValue("INTC")).thenReturn(123.45F);
            //when(dataServices.getClients()).thenReturn(clients);

        } catch (Exception e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }
    }

    @Test
    public void testReviewBuyOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCliente(1);
        order.setAccionId(3);
        order.setOperacion(Operacion.COMPRA);
        order.setCantidad(10);
        order.setModo(Modo.AHORA);
        order.setFechaOperacion(
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault())
        );

        Order result = reviewOrderService.reviewOrder(order);

        assertEquals(1, result.getId());
        assertEquals(1, result.getCliente());
        assertEquals(3, result.getAccionId());
        assertEquals("INTC", result.getAccion()); // Se verifica que el codigo de la accion sea el mismo que el de la accion y se adiciona
        assertEquals(Operacion.COMPRA, result.getOperacion());
        assertEquals(123.45F, result.getPrecio()); // Se verifica que el precio sea el ultimo precio de la accion
        assertEquals(10, result.getCantidad()); // Se verifica que la cantidad sea calida para comprar
        assertEquals(Modo.AHORA, result.getModo());
        assertEquals(
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.getFechaOperacion()
        );
        assertEquals(Estado.PENDIENTE, result.getEstado()); // Se coloca el estado como pendiente
    }

    @Test
    public void testReviewSellOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCliente(1);
        order.setAccionId(3);
        order.setOperacion(Operacion.VENTA);
        order.setCantidad(10);
        order.setModo(Modo.FINDIA);
        order.setFechaOperacion(
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault())
        );

        Order result = reviewOrderService.reviewOrder(order);

        assertEquals(1, result.getId());
        assertEquals(1, result.getCliente());
        assertEquals(3, result.getAccionId());
        assertEquals("INTC", result.getAccion()); // Se verifica que el codigo de la accion sea el mismo que el de la accion y se adiciona
        assertEquals(Operacion.VENTA, result.getOperacion());
        assertEquals(123.45F, result.getPrecio()); // Se verifica que el precio sea el ultimo precio de la accion
        assertEquals(10, result.getCantidad()); // Se verifica que la cantidad sea calida para comprar
        assertEquals(Modo.FINDIA, result.getModo());
        assertEquals(
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.getFechaOperacion()
        );
        assertEquals(Estado.PENDIENTE, result.getEstado()); // Se coloca el estado como pendiente
    }

    @Test
    public void testReviewBadSellOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCliente(1);
        order.setAccionId(3);
        order.setOperacion(Operacion.VENTA);
        order.setCantidad(50);
        order.setModo(Modo.FINDIA);
        order.setFechaOperacion(
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault())
        );

        assertThrows(
            BadRequestAlertException.class,
            () -> {
                reviewOrderService.reviewOrder(order);
            }
        );
    }
}
