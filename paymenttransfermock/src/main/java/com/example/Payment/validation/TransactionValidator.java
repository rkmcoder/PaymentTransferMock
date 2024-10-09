package com.example.Payment.validation;

import com.example.Payment.dao.TransactionRepository;
import com.example.Payment.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator {

    private static TransactionRepository transactionRepository;

    public static boolean isValidTransactionId(String transactionId) {

        Transaction transaction = transactionRepository.findTransactionById(transactionId);
        return transaction != null && transaction.getAmount() > 0;
    }
}
