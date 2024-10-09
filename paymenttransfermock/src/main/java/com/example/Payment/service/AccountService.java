package com.example.Payment.service;

import com.example.Payment.dao.AccountRepository;
import com.example.Payment.exception.AccountNotFoundExceptionException;
import com.example.Payment.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Double getAccountBalance(String accountId) throws AccountNotFoundExceptionException {
        Account account = accountRepository.findAccountById(accountId);

        // If the account exists, return the balance
        if (account != null) {
            return account.getBalance();
        }
        else
            throw new AccountNotFoundExceptionException("Account does not exists");
    }
}
