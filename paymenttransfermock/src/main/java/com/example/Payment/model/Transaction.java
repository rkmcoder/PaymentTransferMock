package com.example.Payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private String id;
    private String bankAccount;
    private String glWalletId;
    private double amount;
    private String status;
    private boolean isRetried;
}
