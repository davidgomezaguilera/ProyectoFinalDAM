package com.alexfu.formvalidator.rules;

public class SonIguales implements ValidationRule {
    private String errorMessage;

    public SonIguales( String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isValid(String input) {
        return false;
    }
}
