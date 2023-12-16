package com.progii.finalcom.service;

import com.progii.finalcom.aop.logging.ColorLogs;
import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
import com.progii.finalcom.service.AditionalServices;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class OrderReportingService {

    @Autowired
    private final SuccessfulOrdersRepository successfulOrdersRepository;

    private final String reportingEndpoint = "http://192.168.194.254:8000/api/reporte-operaciones/reportar";

    @Autowired
    private RestTemplate restTemplate; // Inyecta el RestTemplate de la configuración

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    public OrderReportingService(SuccessfulOrdersRepository successfulOrdersRepository, RestTemplate restTemplate) {
        this.successfulOrdersRepository = successfulOrdersRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "0 * * * * *") // Se ejecuta cada hora
    public void reportOrders() {
        List<SuccessfulOrders> pendingOrders = successfulOrdersRepository.findByEstadoFalse();

        Map<String, List<Map<String, Object>>> responseMap = AditionalServices.formatList(pendingOrders);

        System.out.println(ColorLogs.GREEN + responseMap + ColorLogs.RESET);

        // Configurar el encabezado de autorización
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Map<String, List<Map<String, Object>>>> request = new HttpEntity<>(responseMap, headers);

        // Enviar el informe al servidor de la cátedra
        restTemplate.postForEntity(reportingEndpoint, request, Void.class);

        // Actualizar el estado de las órdenes a `true`
        updateOrderStatus(pendingOrders);
    }

    private void updateOrderStatus(List<SuccessfulOrders> orders) {
        for (SuccessfulOrders order : orders) {
            order.setEstado(true);
        }
        successfulOrdersRepository.saveAll(orders);
    }
}
