package com.solvtrends.monolito.response;

import java.time.Instant;

public class ApiResponse<T> {
    private String timestamp;
    private int status;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

