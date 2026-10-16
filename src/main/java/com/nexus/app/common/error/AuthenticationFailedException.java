package com.nexus.app.common.error;

/** Generic login failure. The message is identical for every cause on purpose. */
public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Invalid email or password");
    }
}