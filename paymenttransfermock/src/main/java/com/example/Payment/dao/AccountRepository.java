package com.example.Payment.dao;

import com.example.Payment.model.Account;

public interface AccountRepository {
    Account findAccountById(String accountId);
    void saveAccount(Account account);
}
