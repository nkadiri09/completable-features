# Async API Calls with CompletableFuture in Java

## Overview
This guide explains how to use `CompletableFuture` in Java to make asynchronous REST API calls using `RestTemplate`. It covers different methods like `thenCompose()`, `thenCombine()`, and `allOf()` to handle dependent and independent API calls efficiently.

## Prerequisites
- Java 8 or later
- Spring Boot (optional for `RestTemplate` usage)
- Internet connection to call example APIs

---

## 1️⃣ Chaining Dependent API Calls with `thenCompose()`
### **Use Case:** Call the second API only after the first API succeeds.
```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;

public class ThenComposeExample {
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        callFirstApi()
            .thenCompose(result -> callSecondApi(result))
            .thenAccept(response -> System.out.println("Final Response: " + response))
            .exceptionally(ex -> {
                System.err.println("Error: " + ex.getMessage());
                return null;
            });
    }

    private static CompletableFuture<String> callFirstApi() {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://jsonplaceholder.typicode.com/posts/1";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        });
    }

    private static CompletableFuture<String> callSecondApi(String input) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://jsonplaceholder.typicode.com/posts";
            ResponseEntity<String> response = restTemplate.postForEntity(url, "{\"title\":\"" + input + "\"}", String.class);
            return response.getBody();
        });
    }
}
```
✅ **Use `thenCompose()` when the second API depends on the first API’s result.**

---

## 2️⃣ Combining Independent API Calls with `thenCombine()`
### **Use Case:** Fetch user info and account balance separately, then merge results.
```java
import java.util.concurrent.CompletableFuture;

public class ThenCombineExample {
    public static void main(String[] args) {
        CompletableFuture<String> userDetails = CompletableFuture.supplyAsync(() -> "User: John");
        CompletableFuture<String> accountDetails = CompletableFuture.supplyAsync(() -> "Balance: $1000");

        CompletableFuture<String> combined = userDetails.thenCombine(accountDetails, (user, account) -> user + ", " + account);

        System.out.println(combined.join()); // Output: User: John, Balance: $1000
    }
}
```
✅ **Use `thenCombine()` when two independent tasks need to be merged.**

---

## 3️⃣ Running Multiple API Calls in Parallel with `allOf()`
### **Use Case:** Fetch data from multiple APIs in parallel and wait for all to complete.
```java
import java.util.concurrent.CompletableFuture;

public class AllOfExample {
    public static void main(String[] args) {
        CompletableFuture<String> api1 = CompletableFuture.supplyAsync(() -> "API 1 Result");
        CompletableFuture<String> api2 = CompletableFuture.supplyAsync(() -> "API 2 Result");

        CompletableFuture<Void> all = CompletableFuture.allOf(api1, api2);
        all.join(); // Waits for both to complete

        System.out.println(api1.join() + " & " + api2.join());
        // Output: API 1 Result & API 2 Result
    }
}
```
✅ **Use `allOf()` when you just need to wait for multiple API calls without combining results.**

---

## 🆚 `thenCompose()` vs. `thenCombine()` vs. `allOf()`
| Feature           | `thenCompose()` | `thenCombine()` | `allOf()` |
|------------------|----------------|----------------|-----------|
| Runs tasks in parallel? | ❌ No (sequential) | ✅ Yes | ✅ Yes |
| Returns a value? | ✅ Yes (`CompletableFuture<T>`) | ✅ Yes (`CompletableFuture<T>`) | ❌ No (`CompletableFuture<Void>`) |
| Automatically merges results? | ❌ No | ✅ Yes | ❌ No (manual retrieval needed) |
| When to use? | Dependent tasks | Independent tasks | Run multiple tasks in parallel |

---

## ✅ Conclusion
- **Use `thenCompose()`** when the **second API depends on the first API’s result**.
- **Use `thenCombine()`** when two **independent API calls** need to be merged.
- **Use `allOf()`** when you need to **run multiple API calls in parallel but don’t need to merge results**.

🚀 **Choose the right method based on task dependency and result combination needs!**

