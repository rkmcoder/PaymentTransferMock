package com.example.Payment.exception;

public class TransferFailedException extends Exception {
    public TransferFailedException(String message) {
        super(message);
    }
}
