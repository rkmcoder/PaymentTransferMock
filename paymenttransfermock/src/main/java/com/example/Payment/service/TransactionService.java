package com.example.Payment.service;

import com.example.Payment.dao.TransactionRepository;
import com.example.Payment.exception.TransferFailedException;
import com.example.Payment.model.ApiResponse;
import com.example.Payment.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ApiService apiService;

    // Timeout Executor (optional)
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void transferAmount(String bankAccount, String glWalletId, double amount) throws TransferFailedException {
        // Simulating network/service delay or timeout
        try {
            Future<?> future = executorService.submit(() -> performTransfer(bankAccount, glWalletId, amount));
            future.get(5, TimeUnit.SECONDS); // Timeout after 5 seconds
        } catch (TimeoutException e) {
            throw new TransferFailedException("Transfer timed out");
        } catch (ExecutionException | InterruptedException e) {
            throw new TransferFailedException("Transfer failed due to system error: " + e.getMessage());
        }
    }

    public boolean performTransfer(String bankAccount, String glWalletId, double amount) throws TransferFailedException {

        String id = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(id, bankAccount, glWalletId, amount, "PENDING", false);
        transactionRepository.saveTransaction(transaction);

        return startTransfer(transaction);

    }

    // Recovery method if retries are exhausted
    @Recover
    public void recover(TransferFailedException e, String userId, String bankAccount, String glWalletId, double amount) {
        System.out.println("Recovery logic after retries exhausted for user: " + userId);
        // Implement any compensation or fallback logic here
    }

    public boolean startTransfer(Transaction transaction) throws TransferFailedException {

        ApiResponse debitApiresponse = apiService.debitFromBankAccount(transaction.getBankAccount(), transaction.getAmount());

        if (debitApiresponse.isSuccess()) {
            ApiResponse creditApiresponse = apiService.creditToGLWallet(transaction.getGlWalletId(), transaction.getAmount());// handle resiliency(cb), timeout,exceptions, retries

            if(creditApiresponse.isSuccess())
                transactionRepository.updateTransactionStatus(transaction.getId(), "SUCCESS");
            else{
                ApiResponse rollbackResponse = apiService.creditToBankAccount(transaction.getBankAccount(), transaction.getAmount());
                if(rollbackResponse.isSuccess())
                    handleFailure(transaction, rollbackResponse.getMessage());
                else throw new TransferFailedException("Rollback of debit failed from bank");

                throw new TransferFailedException(("Credit to wallet has failed. Money debited has been credit back to bank"));

            }


        } else {
            handleFailure(transaction, debitApiresponse.getMessage());
            throw new TransferFailedException("Debit failed from bank");
        }

        return true;
    }

    private void handleFailure(Transaction transaction, String errorMessage) {
        // Update status as failed in the repository
        transactionRepository.updateTransactionStatus(transaction.getId(), "FAILED");
        System.out.println("Transaction failed: " + errorMessage);
    }

    public Transaction getTransaction(String transactionId) {
        return transactionRepository.getTransaction(transactionId);
    }


    // Idempotent retry logic for a failed transaction
    public boolean retryTransaction(String transactionId) throws TransferFailedException {
        Transaction transaction = transactionRepository.findTransactionById(transactionId);

        // If the transaction doesn't exist or isn't marked as "FAILED", return false
        if (transaction == null || !transaction.getStatus().equals("FAILED")) {
            return false;
        }

        // Check if the transaction has already been retried (to ensure idempotency)
        if (transaction.isRetried()) {
            return true; // No need to retry again; it's already been processed
        }
        if(startTransfer(transaction)) {
            transaction.setRetried(true); // Mark the transaction as retried
            transactionRepository.updateTransaction(transaction);
            return true;
        }

        return false;
    }
}
