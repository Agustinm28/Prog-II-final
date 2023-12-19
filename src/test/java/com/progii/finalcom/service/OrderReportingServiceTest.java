package com.progii.finalcom.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.domain.enumeration.Modo;
import com.progii.finalcom.domain.enumeration.Operacion;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class OrderReportingServiceTest {

    @Value("${urls.serviciocatedra}")
    public String url;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SuccessfulOrdersRepository successfulOrdersRepository;

    @InjectMocks
    private OrderReportingService service;

    private SuccessfulOrders order = new SuccessfulOrders();

    private SuccessfulOrders order2 = new SuccessfulOrders();

    private List<SuccessfulOrders> orders = new ArrayList();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            String reportingEndpoint = url + "reporte-operaciones/reportar";

            // Configuración de order
            order.setId(1L);
            order.setCliente(1);
            order.setAccion("AAPL");
            order.setOperacion(Operacion.COMPRA);
            order.setCantidad(15);
            order.setPrecio((float) 23.2);
            order.setFechaOperacion(Instant.parse("2023-09-25T03:00:00Z"));
            order.setModo(Modo.AHORA);
            order.setOperacionObservaciones("ok");
            order.setEstado(false);

            // Configuración de order2
            order2.setId(2L);
            order2.setCliente(1);
            order2.setAccion("AAPL");
            order2.setOperacion(Operacion.COMPRA);
            order2.setCantidad(15);
            order2.setPrecio((float) 23.2);
            order2.setFechaOperacion(Instant.parse("2023-09-25T03:00:00Z"));
            order2.setModo(Modo.AHORA);
            order2.setOperacionObservaciones("ok");
            order2.setEstado(false);

            orders.add(order);
            orders.add(order2);

            when(successfulOrdersRepository.findByEstadoFalse()).thenReturn(orders);
            System.out.println(orders.size());

            System.out.println(successfulOrdersRepository == service.successfulOrdersRepository);
            System.out.println(successfulOrdersRepository.findByEstadoFalse().size());

            ResponseEntity<Void> mockResponseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);
            when(restTemplate.postForEntity(eq(reportingEndpoint), any(HttpEntity.class), eq(Void.class))).thenReturn(mockResponseEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReportOrders() {
        service.reportOrders();

        assertNotNull(orders);

        for (SuccessfulOrders ordenes : orders) {
            assertTrue(ordenes.getEstado());
        }
    }
}
