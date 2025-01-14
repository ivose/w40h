package com.example.some.exception;

import com.example.some.dto.ErrorMessageDTO;
import com.example.some.util.constants.MessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;


import java.time.format.DateTimeParseException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageDTO> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        if (ex.getMessage().equals("Please confirm your email address before logging in")) {
            errorMessage.setName(ex.getMessage());  // Use the exact inactive account message
        } else {
            errorMessage.setName(MessageConstants.INVALID_CREDENTIALS);
        }
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessageDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        errorMessage.setName(MessageConstants.USER_NOT_FOUND);
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessageDTO> handleRuntimeException(RuntimeException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        errorMessage.setName(ex.getMessage());
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessageDTO> handleIllegalStateException(IllegalStateException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        // Check if it's the token expiration message
        if (ex.getMessage().equals("Token expired")) {
            errorMessage.setName("Password reset token has expired. Please request a new password reset");
        } else {
            errorMessage.setName(ex.getMessage());
        }
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessageDTO> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        errorMessage.setName("Access denied. You do not have the required permissions.");
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.FORBIDDEN.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorMessageDTO> handleDateTimeParseException(DateTimeParseException ex) {
        ErrorMessageDTO errorMessage = new ErrorMessageDTO();
        errorMessage.setName("Invalid date format. Please use format YYYY-MM-DD");
        errorMessage.setDate(new Date());
        errorMessage.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}