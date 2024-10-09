package com.example.Payment.controller;

import com.example.Payment.exception.AccountNotFoundExceptionException;
import com.example.Payment.exception.TransferFailedException;
import com.example.Payment.exception.ValidationException;
import com.example.Payment.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.Payment.service.TransactionService;

import static com.example.Payment.validation.AccountValidator.isValidBalance;
import static com.example.Payment.validation.AccountValidator.validateAccountId;
import static com.example.Payment.validation.TransactionValidator.isValidTransactionId;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("transferAmount")
    public ResponseEntity<String> transferAmount(
            @RequestParam String parentGlWalletId,
            @RequestParam String childGlWalletId,
            @RequestParam double amount) {

        try {
            validateAccountId(parentGlWalletId);
            validateAccountId(childGlWalletId);
            isValidBalance(parentGlWalletId, amount);
        }
        catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (AccountNotFoundExceptionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        // Call the service layer to perform the transfer
        try {
            transactionService.transferAmount(parentGlWalletId, childGlWalletId, amount);
        }
        catch (TransferFailedException e){
            ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Transfer successful");
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<String> getTransactionStatus(@PathVariable String transactionId) {
        if (!isValidTransactionId(transactionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid transaction ID format");
        }

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transaction not found");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Transaction status: " + transaction.getStatus());
    }

    // Method to validate transaction ID format (basic validation example)


    @PostMapping("/retry/{transactionId}")
    public ResponseEntity<String> retryFailedTransaction(@PathVariable String transactionId) {
        // Validate transaction ID format
        if (!isValidTransactionId(transactionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction ID");
        }

        // Process the retry logic
        try {
            boolean retrySuccess = transactionService.retryTransaction(transactionId);
            if (retrySuccess) {
                return ResponseEntity.status(HttpStatus.OK).body("Retry successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Retry failed due to issues");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrying transaction: " + e.getMessage());
        }
    }

}
