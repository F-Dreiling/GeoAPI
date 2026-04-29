package dev.dreiling.GeoAPI.config;

import dev.dreiling.GeoAPI.location.UploadResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.ResponseEntity;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<UploadResponse> handleMaxSize( MaxUploadSizeExceededException ex ) {

        UploadResponse response = new UploadResponse(
                null,
                "error",
                "Image upload rejected: Image too large (max 2MB)"
        );

        return ResponseEntity.ok( response );
    }
}