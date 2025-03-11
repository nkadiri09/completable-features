package com.naren.personal.completablefeatures.async;

import java.util.concurrent.CompletableFuture;

public class AllOfExample {

    public static void main(String[] args) {

        /**
         * allOf() → Waits for both to complete, but doesn’t combine results
         * Runs both API calls in parallel.
         * Does not return their results directly (only returns CompletableFuture<Void>).
         * You must manually retrieve results using .join().
         * 🔹 Example: Fetch data from two APIs and just wait for completion
         */

        CompletableFuture<String> api1 = CompletableFuture.supplyAsync(() -> "API 1 Result");
        CompletableFuture<String> api2 = CompletableFuture.supplyAsync(() -> "API 2 Result");

        CompletableFuture<Void> all = CompletableFuture.allOf(api1, api2);
        all.join(); // Waits for both to complete

        System.out.println(api1.join() + " & " + api2.join());


    }
}
