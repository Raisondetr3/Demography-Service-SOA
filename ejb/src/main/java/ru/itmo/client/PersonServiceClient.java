package ru.itmo.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import ru.itmo.dto.PersonDTO;

import javax.ejb.Stateless;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Stateless
public class PersonServiceClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public PersonServiceClient() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        this.httpClient = HttpClient.newBuilder()
//                .connectTimeout(Duration.ofSeconds(5))
//                .build();

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new UnsafeTrustManager()}, null);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .sslContext(sslContext)
                .build();
    }

    public List<PersonDTO> getAllPersons() {
        String baseUrl = "https://localhost:58123";
        String uri = baseUrl + "/persons?page=0&size=1000000000";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<PersonDTO>>() {
                });
            } else {
                throw new RuntimeException("Failed to fetch persons: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during HTTP request", e);
        }
    }
}