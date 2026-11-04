package com.example.booking.common.exception;

import java.time.Instant;

public class ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {

}
