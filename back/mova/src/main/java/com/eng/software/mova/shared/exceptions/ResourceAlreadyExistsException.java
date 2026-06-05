package com.eng.software.mova.shared.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends ApiException {
    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

