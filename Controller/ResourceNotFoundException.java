package com.example.NoteTakingApp.Controller; // Make sure package matches your structure

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// The @ResponseStatus annotation tells Spring to automatically return 404 NOT FOUND
// when this exception is thrown and not caught elsewhere (e.g., by a @ControllerAdvice).
// This can simplify your controller catch blocks if you only want the status code.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    // You can add other constructors if needed
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}