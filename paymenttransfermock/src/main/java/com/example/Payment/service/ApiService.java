package com.example.Payment.service;

import com.example.Payment.exception.TransferFailedException;
import com.example.Payment.model.ApiResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    private static final String CIRCUIT_BREAKER_NAME = "externalApiCircuitBreaker";

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackSimulateExternalApiCall")
    @Retryable(
            value = { TransferFailedException.class }, // The exception to retry on
            maxAttempts = 3,// Maximum retry attempts
            backoff = @Backoff(delay = 2000)            // Backoff time between retries (e.g., 2 seconds)
    )
    public ApiResponse debitFromBankAccount(String accountId, double amount) {
        // Simulate external API call
        System.out.println("Invoking Credit API for Transaction: " + accountId);
        // Simulate success/failure
        try {
            String success = simulateExternalApiCall();
        }
        catch (RuntimeException e) {
            return new ApiResponse(false, "Credit Failed");
        }
        return new ApiResponse(true , "Credit Successful");
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackSimulateExternalApiCall")
    @Retryable(
            value = { TransferFailedException.class }, // The exception to retry on
            maxAttempts = 3,// Maximum retry attempts
            backoff = @Backoff(delay = 2000)            // Backoff time between retries (e.g., 2 seconds)
    )
    public ApiResponse creditToGLWallet(String accountId, double amount) {
        // Simulate external API call
        System.out.println("Invoking Debit API for Transaction: " + accountId);
        // Simulate success/failure
        try {
            String success = simulateExternalApiCall();
        }
        catch (RuntimeException e) {
            return new ApiResponse(false, "Debit Failed");
        }
        return new ApiResponse(true , "Debit Successful");
    }
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallbackSimulateExternalApiCall")
    @Retryable(
            value = { TransferFailedException.class }, // The exception to retry on
            maxAttempts = 3,// Maximum retry attempts
            backoff = @Backoff(delay = 2000)            // Backoff time between retries (e.g., 2 seconds)
    )
    public ApiResponse creditToBankAccount(String accountId, double amount) {
        System.out.println("Invoking Debit API for Transaction: " + accountId);
        // Simulate success/failure
        try {
            String success = simulateExternalApiCall();
        }
        catch (RuntimeException e) {
            return new ApiResponse(false, "Debit Failed");
        }
        return new ApiResponse(true , "Debit Successful");
    }

    public String simulateExternalApiCall() {
        // Simulating an external API call (this can fail or be slow)
        System.out.println("Calling external API...");

        // Simulate a failure scenario (e.g., throw exception 50% of the time)
        if (Math.random() < 0.5) {
            throw new RuntimeException("External API failure");
        }

        // Simulate success
        return "API call successful";
    }

    // Fallback method if the Circuit Breaker is OPEN or the call fails
    public String fallbackSimulateExternalApiCall(Throwable t) {
        System.out.println("Fallback called due to: " + t.getMessage());
        return "Fallback response: External API is unavailable";
    }
}
