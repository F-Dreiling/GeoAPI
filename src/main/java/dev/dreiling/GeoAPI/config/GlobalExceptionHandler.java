package dev.dreiling.GeoAPI.config;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSize(MaxUploadSizeExceededException ex) {

        return ResponseEntity.status( HttpStatus.PAYLOAD_TOO_LARGE ).body( "Image too large. Max allowed size is 2MB." );
    }
}