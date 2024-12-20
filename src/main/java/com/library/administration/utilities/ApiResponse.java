package com.library.administration.utilities;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private String message;
    private T result;

    public ApiResponse(String message, T result) {
        this.message = message;
        this.result = result;
    }
}
