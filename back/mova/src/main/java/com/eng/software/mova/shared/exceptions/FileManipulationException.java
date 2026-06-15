package com.eng.software.mova.shared.exceptions;

import org.springframework.http.HttpStatus;

public class FileManipulationException extends ApiException{
    public FileManipulationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
