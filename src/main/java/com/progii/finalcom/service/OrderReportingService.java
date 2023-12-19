package com.progii.finalcom.service;

import com.progii.finalcom.aop.logging.ColorLogs;
import com.progii.finalcom.domain.SuccessfulOrders;
import com.progii.finalcom.repository.SuccessfulOrdersRepository;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
public class OrderReportingService {

    private final Logger log = LoggerFactory.getLogger(OrderReportingService.class);

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");
    String urls = dotenv.get("URLSERVER");

    @Value("${urls.serviciocatedra}")
    String url;

    @Autowired
    public final SuccessfulOrdersRepository successfulOrdersRepository;

    private final String reportingEndpoint = urls + "reporte-operaciones/reportar";

    @Autowired
    private RestTemplate restTemplate;

    public OrderReportingService(SuccessfulOrdersRepository successfulOrdersRepository, RestTemplate restTemplate) {
        this.successfulOrdersRepository = successfulOrdersRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "0 * * * * *")
    public void reportOrders() {
        List<SuccessfulOrders> pendingOrders = successfulOrdersRepository.findByEstadoFalse();

        if (!pendingOrders.isEmpty()) {
            Map<String, List<SuccessfulOrders>> responseMap = new LinkedHashMap<>();

            responseMap.put("ordenes", pendingOrders);

            log.info(ColorLogs.BLUE + responseMap + ColorLogs.RESET);

            // Encabezado de autorización
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Map<String, List<SuccessfulOrders>>> request = new HttpEntity<>(responseMap, headers);

            // Enviar
            restTemplate.postForEntity(reportingEndpoint, request, Void.class);

            log.info(ColorLogs.GREEN + "Accepted" + ColorLogs.RESET);

            // Actualizar a true
            System.out.println(pendingOrders.size());
            updateOrderStatus(pendingOrders);
        } else {
            log.info(ColorLogs.YELLOW + "Nothing to update" + ColorLogs.RESET);
        }
    }

    private void updateOrderStatus(List<SuccessfulOrders> orders) {
        System.out.println(orders.size());
        for (SuccessfulOrders order : orders) {
            order.setEstado(true);
        }
        successfulOrdersRepository.saveAll(orders);
    }
}
