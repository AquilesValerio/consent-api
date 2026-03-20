package com.sensedia.consentapi.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;

@RestControllerAdvice


public class ControllerExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardError> badRequest(IllegalStateException e) {
        int status = HttpStatus.BAD_REQUEST.value();
        StandardError err = new StandardError();


        err.setCode(status);
        err.setTitle("**");
        err.setDetail(e.getMessage());
        err.setRequestDateTime(LocalDate.now().toString());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> badRequest(MethodArgumentNotValidException e) {
        int status = HttpStatus.BAD_REQUEST.value();
        StandardError err = new StandardError();

        String mensagemError = e.getBindingResult().getFieldError().getDefaultMessage();

        err.setCode(status);
        err.setTitle("CPF inválido");
        err.setDetail(mensagemError);
        err.setRequestDateTime(LocalDate.now().toString());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFound(ResourceNotFoundException e){
        StandardError err = new StandardError();
        HttpStatus status = HttpStatus.NOT_FOUND;

        err.setCode(status.value());
        err.setTitle("Not found");
        err.setDetail(e.getMessage());
        err.setRequestDateTime(LocalDate.now().toString());

        return ResponseEntity.status(status).body(err);
    }


}
