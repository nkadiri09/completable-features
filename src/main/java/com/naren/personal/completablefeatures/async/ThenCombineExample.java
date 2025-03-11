package com.naren.personal.completablefeatures.async;

import java.util.concurrent.CompletableFuture;

public class ThenCombineExample {

    public static void main(String[] args) {

        /**
         * thenCombine() → Combines results of two independent futures
         * Both calls run in parallel.
         * Once both are complete, their results are combined into a single value.
         * Used when you need to merge the results of two independent computations.
         * 🔹 Example: Fetch user info & account balance, then merge the results
         */


        CompletableFuture<String> userDetails = CompletableFuture.supplyAsync(() -> "User: John");
        CompletableFuture<String> accountDetails = CompletableFuture.supplyAsync(() -> "Balance: $1000");

        CompletableFuture<String> combined = userDetails.thenCombine(accountDetails,
                (user, account) -> user + ", " + account
        );
        System.out.println(combined.join());

    }


}
