package com.naren.personal.completablefeatures.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AsyncCompletableFutureExample {

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Future 1: Starting computation in thread " + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int result = ThreadLocalRandom.current().nextInt(1, 100);
            System.out.println("Future 1: Computation complete, result: " + result);
            return result;
        }, executor);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Future 2: Starting computation in thread " + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3)); // Simulate some work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                Thread.currentThread().sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int result = ThreadLocalRandom.current().nextInt(1, 100);
            System.out.println("Future 2: Computation complete, result: " + result);
            return result;
        }, executor);

        CompletableFuture<Integer> combinedFuture = future1.thenCombineAsync(future2, (result1, result2) -> {
            System.out.println("Combined Future: Combining results in thread " + Thread.currentThread().getName());
            return result1 + result2;
        }, executor);

        combinedFuture.thenAcceptAsync(sum -> {
            System.out.println("Final Result: Sum of results: " + sum + " in thread " + Thread.currentThread().getName());
        }, executor);

        // Allow some time for the asynchronous tasks to complete
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);

        System.out.println("Main thread finished.");
    }
}