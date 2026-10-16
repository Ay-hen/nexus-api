package com.nexus.app.common.error;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Session is invalid or expired");
    }
}