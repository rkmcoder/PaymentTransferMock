package com.example.Payment.controller;

import com.example.Payment.exception.AccountNotFoundExceptionException;
import com.example.Payment.exception.ValidationException;
import com.example.Payment.service.AccountService;
import com.example.Payment.validation.AccountValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<String> getAccountBalance(@PathVariable String accountId) {

        // Get account balance from the service layer
        try {
            AccountValidator.validateAccountId(accountId);
            Double balance = accountService.getAccountBalance(accountId);
            if (balance != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Account balance: " + balance);
            }
        }
        catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (AccountNotFoundExceptionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account balance: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving account balance: ");
    }
}
