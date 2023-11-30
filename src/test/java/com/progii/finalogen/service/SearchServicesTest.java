package com.progii.finalogen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Modo;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.repository.OrderRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class SearchServicesTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SearchServices searchServices;

    // Clase que se ejecuta antes de cada test
    @Before
    public void init() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this);

        // Configurar comportamiento del mock
        Order order1 = new Order();
        order1.setId(1L);
        order1.setCliente(1102);
        order1.setAccionId(1);
        order1.setAccion("APPL");
        order1.setOperacion(Operacion.COMPRA);
        order1.setModo(Modo.AHORA);
        order1.setFechaOperacion(
            LocalDateTime.parse("2023-11-29 12:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault())
        );
        order1.setCantidad(10);
        order1.setPrecio(120.5F);
        order1.setEstado(Estado.PENDIENTE);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setCliente(1102);
        order2.setAccionId(1);
        order2.setAccion("APPL");
        order2.setOperacion(Operacion.VENTA);
        order2.setModo(Modo.INICIODIA);
        order2.setFechaOperacion(
            LocalDateTime.parse("2023-11-29 09:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault())
        );
        order2.setCantidad(15);
        order2.setPrecio(86.5F);
        order2.setEstado(Estado.ENVIADO);

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
    }

    @Test
    public void testSearchByFilterCompra() {
        // Ejecutar el metodo a testear
        List<Order> result = searchServices.searchByFilter("1102", "APPL", "1", "COMPRA", "PENDIENTE", null, null);

        // print result
        System.out.println("Result: ");
        System.out.println(result);

        // Verificar resultado
        assertEquals(1, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(1102, result.get(0).getCliente());
        assertEquals(1, result.get(0).getAccionId());
        assertEquals("APPL", result.get(0).getAccion());
        assertEquals(Operacion.COMPRA, result.get(0).getOperacion());
        assertEquals(Modo.AHORA, result.get(0).getModo());
        assertEquals(
            LocalDateTime.parse("2023-11-29 12:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()),
            result.get(0).getFechaOperacion()
        );
        assertEquals(10, result.get(0).getCantidad());
        assertEquals(120.5F, result.get(0).getPrecio());
        assertEquals(Estado.PENDIENTE, result.get(0).getEstado());
    }

    @Test
    public void testSearchByFilterVenta() {
        // Ejecutar el metodo a testear
        List<Order> result = searchServices.searchByFilter("1102", "APPL", "1", "VENTA", "ENVIADO", null, null);

        // print result
        System.out.println("Result: ");
        System.out.println(result);

        // Verificar resultado
        assertEquals(1, result.size());

        assertEquals(2L, result.get(0).getId());
        assertEquals(1102, result.get(0).getCliente());
        assertEquals(1, result.get(0).getAccionId());
        assertEquals("APPL", result.get(0).getAccion());
        assertEquals(Operacion.VENTA, result.get(0).getOperacion());
        assertEquals(Modo.INICIODIA, result.get(0).getModo());
        assertEquals(
            LocalDateTime.parse("2023-11-29 09:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()),
            result.get(0).getFechaOperacion()
        );
        assertEquals(15, result.get(0).getCantidad());
        assertEquals(86.5F, result.get(0).getPrecio());
        assertEquals(Estado.ENVIADO, result.get(0).getEstado());
    }
}
