package com.eng.software.mova.shared.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidAuthenticationException extends ApiException {
    public InvalidAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
