package com.example.Payment.dao;

import com.example.Payment.model.Account;import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account> accountMap = Map.of("1",new Account("1", 1000.0),
            "2", new Account("2", 1000.0));


    @Override
    public Account findAccountById(String accountId) {
        return accountMap.get(accountId);
    }

    @Override
    public void saveAccount(Account account) {
        accountMap.put(account.getAccountId(), account);
    }
}
