package com.declan.productcatalog.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
    private Map<String, String> errors;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this(status, message);
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }public String getMessage() {
        return message;
    }public LocalDateTime getTimestamp() {
        return timeStamp;
    }public Map<String, String> getErrors() {
        return errors;
    }

    public void setStatus(int status) {
        this.status = status;
    }public void setMessage(String message) {
        this.message = message;
    }public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}