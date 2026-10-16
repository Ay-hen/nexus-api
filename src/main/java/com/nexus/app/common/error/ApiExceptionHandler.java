package com.nexus.app.common.error;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                        "field", e.getField(),
                        "code", String.valueOf(e.getCode()),
                        "message", String.valueOf(e.getDefaultMessage())))
                .toList();
        ProblemDetail p = problem(HttpStatus.BAD_REQUEST, "Validation failed", "VALIDATION_FAILED", "Validation failed");
        p.setProperty("errors", errors);
        return p;
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail handleAuthFailed(AuthenticationFailedException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", "AUTHENTICATION_FAILED", ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail handleInvalidRefresh(InvalidRefreshTokenException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid session", "INVALID_SESSION", ex.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ProblemDetail handleEmailNotVerified(EmailNotVerifiedException ex) {
        return problem(HttpStatus.FORBIDDEN, "Email not verified", "EMAIL_NOT_VERIFIED", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbidden(ForbiddenOperationException ex) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ProblemDetail handleDuplicate(DuplicateKeyException ex) {
        return problem(HttpStatus.CONFLICT, "Conflict", "DUPLICATE",
                "A resource with the same unique value already exists");
    }

    private ProblemDetail problem(HttpStatus status, String title, String code, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setTitle(title);
        p.setProperty("code", code);
        return p;
    }
}