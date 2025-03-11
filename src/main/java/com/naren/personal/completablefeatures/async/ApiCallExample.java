package com.naren.personal.completablefeatures.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ApiCallExample {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        CompletableFuture<ResponseEntity<String>> apiCall1 = callApi("http://localhost:8080/api1");
        CompletableFuture<ResponseEntity<String>> apiCall2 = callApi("http://localhost:8080/api2");

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(apiCall1, apiCall2);

        combinedFuture.thenRun(() -> {
            try {
                ResponseEntity<String> response1 = apiCall1.get();
                ResponseEntity<String> response2 = apiCall2.get();
                System.out.println("API 1 Response: " + response1.getBody());
                System.out.println("API 2 Response: " + response2.getBody());
            } catch (Exception e) {
                System.err.println("Error occurred: " + e.getMessage());
            }
        }).join();

        executorService.shutdown();
    }

    private static CompletableFuture<ResponseEntity<String>> callApi(String url) {
        return CompletableFuture.supplyAsync(() -> restTemplate.getForEntity(url, String.class), executorService);
    }
}