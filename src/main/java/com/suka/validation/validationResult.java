package com.suka.validation;

public class validationResult {
    private boolean valid;
    private String message;

    public validationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
