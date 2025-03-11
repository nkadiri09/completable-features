package com.naren.personal.completablefeatures.async;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiCallWithCompletableFuture {

    private static final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4); // Adjust pool size as needed

    public static CompletableFuture<String> fetchApiResponse(String apiUrl) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(apiUrl)).build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(HttpResponse::body, executor)
                .exceptionally(ex -> {
                    System.err.println("Error fetching API response: " + ex.getMessage());
                    return null; // Or handle the error differently (e.g., return an empty string, throw a custom exception)
                });
    }

    public static void main(String[] args) throws Exception {
        String apiUrl = "https://jsonplaceholder.typicode.com/todos/1"; // Example API

        CompletableFuture<String> apiResponseFuture = fetchApiResponse(apiUrl);

        apiResponseFuture.thenAccept(response -> {
            if (response != null) {
                System.out.println("API Response: " + response);
            } else {
                System.out.println("API call failed.");
            }
        }).join(); // Block until the future completes (for demonstration)

        executor.shutdown();
    }
}