package com.example.Payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {

    private String accountId;
    private Double balance;

}

