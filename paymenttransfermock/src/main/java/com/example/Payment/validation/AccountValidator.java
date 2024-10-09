package com.example.Payment.validation;

import com.example.Payment.dao.AccountRepository;
import com.example.Payment.exception.AccountNotFoundExceptionException;
import com.example.Payment.exception.ValidationException;
import com.example.Payment.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AccountValidator {

    @Autowired
    public static AccountRepository accountRepository;

    public AccountValidator(AccountRepository accountRepository) {
        AccountValidator.accountRepository = accountRepository;
    }

    // Validate account balance
    public static boolean isValidBalance(String accountId, Double balance) throws ValidationException, AccountNotFoundExceptionException {
        Account account = validateAccountId(accountId);
        if(account.getBalance() < balance)
            throw new ValidationException("Insufficient funds");

        else return true;
    }

    public static Account validateAccountId(String accountId) throws ValidationException, AccountNotFoundExceptionException {

        if (!StringUtils.hasText(accountId) || !accountId.matches("\\d+")) {
            throw new ValidationException("Invalid account ID format");
        }

        Account account = accountRepository.findAccountById(accountId);
        if(account == null)
            throw new AccountNotFoundExceptionException("Account not found");

        return account;

    }
}
