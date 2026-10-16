package com.nexus.app.common.error;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Email address is not verified");
    }
}