package com.progii.finalogen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.opencsv.exceptions.CsvException;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Modo;
import com.progii.finalogen.domain.enumeration.Operacion;
import com.progii.finalogen.repository.OrderRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class SearchServicesTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FileLoader fileLoader;

    @InjectMocks
    private SearchServices searchServices;

    // Clase que se ejecuta antes de cada test
    @Before
    public void init() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchByFilterCompra() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(0)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

        // Ejecutar el metodo a testear
        List<Order> result = searchServices.searchByFilter("1102", "APPL", "1", "COMPRA", "PENDIENTE", null, null);

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
            LocalDateTime
                .parse("2023-11-29T12:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.get(0).getFechaOperacion()
        );
        assertEquals(10, result.get(0).getCantidad());
        assertEquals(120.5F, result.get(0).getPrecio());
        assertEquals(Estado.PENDIENTE, result.get(0).getEstado());
    }

    @Test
    public void testSearchByFilterVenta() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(1)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

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
            LocalDateTime
                .parse("2023-11-29T09:30:00.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.get(0).getFechaOperacion()
        );
        assertEquals(15, result.get(0).getCantidad());
        assertEquals(86.5F, result.get(0).getPrecio());
        assertEquals(Estado.ENVIADO, result.get(0).getEstado());
    }

    @Test
    public void testSearchByDateRange() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(2), orders.get(3), orders.get(8)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

        List<Order> result = searchServices.searchByFilter(null, null, null, null, null, "2023-11-24", "2023-11-25 23:59:00");

        assertEquals(3, result.size()); // Hay 3 ordenes en ese rango de fechas

        assertEquals(3L, result.get(0).getId());
        assertEquals(
            LocalDateTime
                .parse("2023-11-24T19:11:06.935193-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.get(0).getFechaOperacion()
        );

        assertEquals(4L, result.get(1).getId());
        assertEquals(
            LocalDateTime
                .parse("2023-11-25T10:36:55.623896-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.get(1).getFechaOperacion()
        );

        assertEquals(9L, result.get(2).getId());
        assertEquals(
            LocalDateTime
                .parse("2023-11-25T00:00:00.236810-03:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                .atZone(ZoneId.systemDefault()),
            result.get(2).getFechaOperacion()
        );
    }

    @Test
    public void testSearchPendingOperations() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(0), orders.get(12), orders.get(14), orders.get(15)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

        List<Order> result = searchServices.searchByFilter(null, null, null, null, "PENDIENTE", null, null);

        assertEquals(4, result.size()); // Hay 4 ordenes pendientes

        assertEquals(1L, result.get(0).getId());
        assertEquals(Estado.PENDIENTE, result.get(0).getEstado());

        assertEquals(13L, result.get(1).getId());
        assertEquals(Estado.PENDIENTE, result.get(1).getEstado());

        assertEquals(15L, result.get(2).getId());
        assertEquals(Estado.PENDIENTE, result.get(2).getEstado());

        assertEquals(16L, result.get(3).getId());
        assertEquals(Estado.PENDIENTE, result.get(3).getEstado());
    }

    @Test
    public void testSearchCanceledOperations() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(2), orders.get(8), orders.get(11), orders.get(13)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

        List<Order> result = searchServices.searchByFilter(null, null, null, null, "CANCELADO", null, null);

        assertEquals(4, result.size()); // Hay 4 ordenes canceladas

        assertEquals(3L, result.get(0).getId());
        assertEquals(Estado.CANCELADO, result.get(0).getEstado());

        assertEquals(9L, result.get(1).getId());
        assertEquals(Estado.CANCELADO, result.get(1).getEstado());

        assertEquals(12L, result.get(2).getId());
        assertEquals(Estado.CANCELADO, result.get(2).getEstado());

        assertEquals(14L, result.get(3).getId());
        assertEquals(Estado.CANCELADO, result.get(3).getEstado());
    }

    @Test
    public void testSearchByClientIDandPendingStatus() {
        try {
            List<Order> orders = fileLoader.loadOrders(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestOrders.csv"
            );

            @SuppressWarnings("unchecked")
            Specification<Order> specification = any(Specification.class);
            when(orderRepository.findAll(specification)).thenReturn(List.of(orders.get(12), orders.get(14), orders.get(15)));
        } catch (IOException | CsvException e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }

        List<Order> result = searchServices.searchByFilter("1", null, null, null, "PENDIENTE", null, null);

        assertEquals(3, result.size()); // Hay 3 ordenes pendientes para el cliente con ID 1

        assertEquals(13L, result.get(0).getId());
        assertEquals(Estado.PENDIENTE, result.get(0).getEstado());

        assertEquals(15L, result.get(1).getId());
        assertEquals(Estado.PENDIENTE, result.get(1).getEstado());

        assertEquals(16L, result.get(2).getId());
        assertEquals(Estado.PENDIENTE, result.get(2).getEstado());
    }
}
