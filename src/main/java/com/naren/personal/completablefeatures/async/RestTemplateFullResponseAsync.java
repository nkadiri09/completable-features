package com.naren.personal.completablefeatures.async;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

public class RestTemplateFullResponseAsync {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static CompletableFuture<ResponseEntity<String>> fetchApiResponseAsync(String apiUrl, HttpMethod method, HttpHeaders headers, String body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                return restTemplate.exchange(apiUrl, method, requestEntity, String.class);
            } catch (Exception e) {
                System.err.println("Error fetching API response: " + e.getMessage());
                return null; // Or handle the error differently
            }
        }, executor);
    }

    public static void main(String[] args) throws Exception {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts"; // Example POST API
        HttpMethod method = HttpMethod.POST;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String body = "{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}";

        CompletableFuture<ResponseEntity<String>> apiResponseFuture = fetchApiResponseAsync(apiUrl, method, headers, body);

        apiResponseFuture.thenAccept(responseEntity -> {
            if (responseEntity != null) {
                System.out.println("Status Code: " + responseEntity.getStatusCode());
                System.out.println("Headers: " + responseEntity.getHeaders());
                System.out.println("Body: " + responseEntity.getBody());
            } else {
                System.out.println("API call failed.");
            }
        }).join();

        executor.shutdown();
    }
}