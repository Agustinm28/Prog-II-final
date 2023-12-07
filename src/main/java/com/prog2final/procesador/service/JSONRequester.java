package com.prog2final.procesador.service;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.prog2final.procesador.config.Constants;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JSONRequester {

    public JSONRequester() {}

    @Transactional
    public String getJSONFromEndpoint(String baseUrl, String endpointSuffix, String authToken) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
            .newBuilder(URI.create(baseUrl + endpointSuffix))
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + authToken)
            .timeout(Duration.of(10, SECONDS))
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException(
                    String.format("Solicitud al endpoint %s%s fallida. Motivo: %s", baseUrl, endpointSuffix, response.body())
                );
            }
            return response.body();
        } catch (IOException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
