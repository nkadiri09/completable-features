package com.naren.personal.completablefeatures.async;

import com.naren.personal.completablefeatures.model.Product;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestTemplateCompletableFutureExample {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2); // Thread pool

    public static void main(String[] args) {
        callFirstApi()
            .thenCompose(result -> callSecondApi(result.getName()))
            .thenAccept(finalResponse -> System.out.println("Final Response: " + finalResponse))
            .exceptionally(ex -> {
                System.err.println("Error occurred: " + ex.getMessage());
                return null;
            });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }

    private static CompletableFuture<Product> callFirstApi() {
        return CompletableFuture.supplyAsync(() -> {
            String url = "http://localhost:8080/products/6";
            ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
            System.out.println("First API Response: " + response.getBody().toString());
            return response.getBody();
        }, executorService).exceptionally(ex -> {
            System.err.println("Error occurred in callFirstApi: " + ex.getMessage());
            return null; // Return null or a default Product object
        });
    }

    private static CompletableFuture<Product> callSecondApi(String inputData) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "http://localhost:8080/products";
            String requestBody = "{\n" +
                    "    \"name\": \"avocado\",\n" +
                    "    \"price\": 1.0\n" +
                    "}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Product> response = restTemplate.postForEntity(url, requestEntity, Product.class);
            System.out.println("Second API Response: " + response.getBody());
            return response.getBody();
        }, executorService);
    }

    private static String extractData(String response) {
        // Simple processing: extract a substring (modify as per actual response structure)
        return response.length() > 20 ? response.substring(0, 20) : response;
    }
}
