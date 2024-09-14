package com.dish.auth.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dish.auth.response.APIResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler({NullPointerException.class})
    public ResponseEntity<APIResponse> handleGlobalException(Exception exception) {
	log.error("Inside global exception handler for Null Pointer exception.");
        APIResponse response =  APIResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(Boolean.FALSE) 
                .message(exception.getMessage()).build();
        return new ResponseEntity<APIResponse>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}