package io.hhplus.tdd;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice{
    @ExceptionHandler(value = CustomPointException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomPointException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ErrorResponse("400", ex.getMessage()));
    }
}
