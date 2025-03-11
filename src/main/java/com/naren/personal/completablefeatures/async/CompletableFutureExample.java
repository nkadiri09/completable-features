package com.naren.personal.completablefeatures.async;

import com.naren.personal.completablefeatures.model.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExample {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static CompletableFuture<ResponseEntity<Product>> fetchApiResponseAsync(String apiUrl, HttpMethod method, HttpHeaders headers, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                return restTemplate.exchange(apiUrl, method, requestEntity, Product.class);
            } catch (Exception e) {
                System.err.println("Error fetching API response: " + e.getMessage());
                return null;
            }
        }, executorService).orTimeout(5, TimeUnit.SECONDS).exceptionally(ex -> {
            System.err.println("Exception: " + ex.getMessage());
            return null;
        });
    }

    public static void main(String[] args) {
        String apiUrl = "http://localhost:8080/products/5";
        HttpMethod method = HttpMethod.GET;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String requestBody = "{\n" +
                "    \"name\": \"avocado\",\n" +
                "    \"price\": 1.0\n" +
                "}";

        CompletableFuture<ResponseEntity<Product>> apiResponseFuture = fetchApiResponseAsync(apiUrl, method, headers, requestBody);

        apiResponseFuture.thenAccept(responseEntity -> {
            if (responseEntity != null) {
                System.out.println("Status Code: " + responseEntity.getStatusCode());
                System.out.println("Headers: " + responseEntity.getHeaders());
                System.out.println("Body: " + responseEntity.getBody());
            } else {
                System.out.println("API call failed.");
            }
        }).join();

        executorService.shutdown();
    }
}