package com.example.Payment.dao;// InMemoryTransactionRepository.java
import com.example.Payment.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private Map<String, Transaction> transactionMap = new HashMap<>();

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionMap.put(transaction.getId(), transaction);
    }

    @Override
    public Transaction getTransaction(String transactionId) {
        return transactionMap.get(transactionId);
    }

    @Override
    public void updateTransactionStatus(String transactionId, String status) {
        Transaction transaction = transactionMap.get(transactionId);
        if (transaction != null) {
            transaction.setStatus(status);
        }
    }


    @Override
    public Transaction findTransactionById(String transactionId) {
        return transactionMap.get(transactionId);
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        transactionMap.put(transaction.getId(), transaction);
    }
}
