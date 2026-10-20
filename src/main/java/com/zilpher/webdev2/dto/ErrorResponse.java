package com.zilpher.webdev2.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    List<FieldErrorDetail> errors
) {}