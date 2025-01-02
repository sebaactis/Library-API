package com.library.administration.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private Integer status;
    private List<FieldErrorsDetails> fieldErrors;

    @Data
    @AllArgsConstructor
    public static class FieldErrorsDetails {
        private String field;
        private String errorMessage;
    }
}
