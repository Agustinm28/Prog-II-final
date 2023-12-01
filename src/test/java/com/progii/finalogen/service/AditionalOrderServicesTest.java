package com.progii.finalogen.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
public class AditionalOrderServicesTest {

    @Mock
    private DataServices dataServices;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FileLoader fileLoader;

    @InjectMocks
    private AditionalOrderServices aditionalOrderServices;

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
            List<Map<String, Object>> clientShares = fileLoader.loadJson(
                "C:\\Users\\agust\\OneDrive\\Documentos\\Github\\Prog-II-final\\src\\main\\resources\\config\\liquibase\\fake-data\\TestClientShares.json"
            );

            when(dataServices.getClients()).thenReturn(clients);
            when(dataServices.getAcciones()).thenReturn(shares);
            when(dataServices.getClientShares(1, 3)).thenReturn(clientShares.get(0));
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo CSV");
            e.printStackTrace();
        }
    }

    @Test
    public void testClientExists() {
        Map<String, Object> result = aditionalOrderServices.clientExists(1);

        System.out.println(result);

        assertEquals(1, result.get("id"));
        assertEquals("María Corvalán", result.get("nombreApellido"));
        assertEquals("Happy Soul", result.get("empresa"));
    }

    @Test
    public void testShareExists() {
        Map<String, Object> result = aditionalOrderServices.accionExists(3);

        System.out.println(result);

        assertEquals(3, result.get("id"));
        assertEquals("INTC", result.get("codigo"));
        assertEquals("Intel Corporation", result.get("empresa"));
    }

    @Test
    public void testClientShares() {
        int result = aditionalOrderServices.sellClientExists(1, 3);

        assertEquals(40, result); // 40 acciones de AAPL
    }
}
