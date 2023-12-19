package com.progii.finalcom.service;

import com.progii.finalcom.web.rest.errors.BadRequestAlertException;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
public class ProxyService {

    private final Logger log = LoggerFactory.getLogger(ProxyService.class);

    RestTemplate restTemplate = new RestTemplate();

    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("TOKEN");

    //Clientes
    public Map<String, List<Map<String, Object>>> getDataMapClientes(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    //Clientes Buscar
    public Object getDataMapClientesBuscar(String endpoint, String nombre, String empresa) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endpoint)
            .queryParamIfPresent("nombre", Optional.ofNullable(nombre))
            .queryParamIfPresent("empresa", Optional.ofNullable(empresa));

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, List<Map<String, Object>>>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, List<Map<String, Object>>>>() {}
            );

            // Extraer el cuerpo
            Map<String, List<Map<String, Object>>> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("clientes")) {
                return responseBody.get("clientes");
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Acciones
    public Object getDataAcciones(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            ResponseEntity<Object> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, Object.class);
            Object responseBody = response.getBody();

            // Verificar el tipo de la respuesta
            if (responseBody instanceof List) {
                // array
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> acciones = (List<Map<String, Object>>) responseBody;
                return acciones;
            } else if (responseBody instanceof Map) {
                // objeto
                @SuppressWarnings("unchecked")
                Map<String, Object> accion = (Map<String, Object>) responseBody;
                return accion;
            } else {
                // otros casos
                log.info("Respuesta inesperada del servidor");
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Acciones Buscar
    public Object buscarAcciones(String endpoint, String empresa, String codigo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endpoint)
            .queryParamIfPresent("empresa", Optional.ofNullable(empresa))
            .queryParamIfPresent("codigo", Optional.ofNullable(codigo));

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<?> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Object.class);

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // Ultimo Valor Acciones
    public Object getUltimoValorAcciones(String endpoint, String codigo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<?> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, Object.class, codigo);

            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    // Ordenes
    public Object getOrdenes(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Object> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, Object.class);

            Object responseData = response.getBody();

            // Verificar el tipo de la respuesta
            if (responseData instanceof List) {
                // lista
                Map<String, List<Object>> dataMap = new HashMap<>();

                dataMap.put("ordenes", (List<Object>) responseData);
                return dataMap;
            } else {
                // objeto
                return responseData;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Reporte Operaciones Consulta
    public Object getReporte(String endpoint, Long clienteId, Long accionId, String fechaInicio, String fechaFin) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Parámetros
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endpoint)
            .queryParamIfPresent("clienteId", Optional.ofNullable(clienteId))
            .queryParamIfPresent("accionId", Optional.ofNullable(accionId))
            .queryParamIfPresent("fechaInicio", Optional.ofNullable(fechaInicio))
            .queryParamIfPresent("fechaFin", Optional.ofNullable(fechaFin));

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Object> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Object.class);

            Object responseData = response.getBody();

            // Verificar el tipo de la respuesta
            if (responseData instanceof List) {
                // Lista
                return responseData;
            } else {
                // Objeto
                List<Object> dataList = new ArrayList<>();
                dataList.add(responseData);
                return dataList;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Reporte Operaciones Consulta Cliente Accion
    public Object getReporteClienteAccion(String endpoint, Long clienteId, Long accionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Parámetros de la consulta
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(endpoint)
            .queryParamIfPresent("clienteId", Optional.ofNullable(clienteId))
            .queryParamIfPresent("accionId", Optional.ofNullable(accionId));

        try {
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

            ResponseEntity<Object> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, Object.class);

            Object responseData = response.getBody();

            // Verificar el tipo de la respuesta
            if (responseData instanceof List) {
                // Lista
                return responseData;
            } else {
                // Objeto
                List<Object> dataList = new ArrayList<>();
                dataList.add(responseData);
                return dataList;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
