package com.example.Payment.dao;

import com.example.Payment.model.Transaction;


public interface TransactionRepository {
    void saveTransaction(Transaction transaction);
    Transaction getTransaction(String transactionId);
    void updateTransactionStatus(String transactionId, String status);
    Transaction findTransactionById(String transactionId);
    void updateTransaction(Transaction transaction);
}
