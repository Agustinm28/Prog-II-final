package com.progii.finalogen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.progii.finalogen.aop.logging.ColorLogs;
import com.progii.finalogen.domain.Order;
import com.progii.finalogen.domain.enumeration.Estado;
import com.progii.finalogen.domain.enumeration.Modo;
import com.progii.finalogen.domain.enumeration.Operacion;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileLoader {

    private final Logger log = LoggerFactory.getLogger(FileLoader.class);

    public List<Order> loadOrders(String path) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new FileReader(path));
        List<String[]> rows = reader.readAll();

        // eliminar header
        rows.remove(0);

        log.info("{}Loading orders from CSV file{}", ColorLogs.YELLOW, ColorLogs.RESET);
        log.info("CSV file has {} rows", rows.size());

        // Convertir cada fila a Order con los campos en el formato correcto
        List<Order> orders = rows
            .stream()
            .map(row -> {
                log.info("{}Row: {}{}", ColorLogs.PURPLE, row, ColorLogs.RESET);

                Order order = new Order();

                order.setId(Long.parseLong(row[0]));
                order.setCliente(Integer.parseInt(row[1]));
                order.setAccionId(Integer.parseInt(row[2]));
                order.setAccion(row[3]);
                Operacion operacion = Operacion.valueOf(row[4]);
                order.setOperacion(operacion);
                Modo modo = Modo.valueOf(row[5]);
                order.setModo(modo);
                order.setFechaOperacion(
                    LocalDateTime
                        .parse(row[6], DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX"))
                        .atZone(ZoneId.systemDefault())
                ); // Importante el formato de fecha
                order.setCantidad(Integer.parseInt(row[7]));
                order.setPrecio(Float.parseFloat(row[8]));
                Estado estado = Estado.valueOf(row[9]);
                order.setEstado(estado);

                return order;
            })
            .toList();

        return orders;
    }

    public List<Map<String, Object>> loadJson(String path) throws Exception {
        // Cargar clientes desde archivo JSON y devolver lista de clientes

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> data = mapper.readValue(new File(path), List.class);

            return data;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        return null;
    }
}
