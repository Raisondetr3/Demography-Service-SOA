package ru.itmo.demography_service.exception.handler;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.itmo.demography_service.dto.ErrorDTO;
import ru.itmo.demography_service.exception.InvalidParameterException;
import ru.itmo.demography_service.exception.PersonServiceException;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonServiceException.class)
    public ResponseEntity<ErrorDTO> handlePersonServiceException(
            PersonServiceException e, HttpServletRequest request) {

        log.error("Person service exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                "EXTERNAL_SERVICE_ERROR",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorDTO> handleFeignException(
            FeignException e, HttpServletRequest request) {

        log.error("Feign exception: status={}", e.status(), e);

        HttpStatus correctStatus = switch (e.status()) {
            case 404, 500 -> HttpStatus.SERVICE_UNAVAILABLE;
            case 408, 504 -> HttpStatus.GATEWAY_TIMEOUT;
            case 502, 503 -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.SERVICE_UNAVAILABLE;
        };

        ErrorDTO error = new ErrorDTO(
                "EXTERNAL_SERVICE_ERROR",
                determineFeignErrorMessage(e),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(correctStatus).body(error);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorDTO> handleInvalidParameterException(
            InvalidParameterException e, HttpServletRequest request) {

        log.warn("Invalid parameter: {} = {}, message: {}",
                e.getParameterName(), e.getParameterValue(), e.getMessage());

        ErrorDTO error = new ErrorDTO(
                "INVALID_REQUEST_PARAMETER",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        log.warn("Method argument type mismatch: parameter={}, value={}, expectedType={}",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        String message;
        if (e.getRequiredType().isEnum()) {
            message = String.format(
                    "Invalid value '%s' for parameter '%s'. Expected one of: %s",
                    e.getValue(), e.getName(),
                    Arrays.toString(e.getRequiredType().getEnumConstants())
            );
        } else {
            message = String.format(
                    "Invalid value '%s' for parameter '%s'. Expected type: %s",
                    e.getValue(), e.getName(), e.getRequiredType().getSimpleName()
            );
        }

        ErrorDTO error = new ErrorDTO(
                "INVALID_PARAMETER_TYPE",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation errors: {}", errors);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "VALIDATION_FAILED");
        response.put("message", "Request body validation failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("HTTP message not readable: {}", e.getMessage());

        String errorCode;
        String message;
        HttpStatus status;

        if (e.getMessage().contains("Required request body is missing")) {
            status = HttpStatus.BAD_REQUEST;
            errorCode = "MISSING_REQUEST_BODY";
            message = "Request body is required but missing";
        } else if (e.getMessage().contains("Cannot deserialize value")) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            errorCode = "INVALID_REQUEST_BODY";
            message = "Invalid data format in request body";
        } else {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            errorCode = "MALFORMED_JSON";
            message = "Invalid JSON format in request body";
        }

        ErrorDTO error = new ErrorDTO(
                errorCode,
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDTO> handleNoHandlerFound(
            NoHandlerFoundException e, HttpServletRequest request) {

        log.warn("No handler found: {}", e.getRequestURL());

        ErrorDTO error = new ErrorDTO(
                "ENDPOINT_NOT_FOUND",
                String.format("No handler found for %s %s", e.getHttpMethod(), e.getRequestURL()),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({TimeoutException.class, SocketTimeoutException.class})
    public ResponseEntity<ErrorDTO> handleTimeoutException(
            Exception e, HttpServletRequest request) {

        log.error("Timeout exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                "TIMEOUT_ERROR",
                "Timeout calling external service",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {

        log.warn("Illegal argument: {}", e.getMessage());

        ErrorDTO error = new ErrorDTO(
                "INVALID_ARGUMENT",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        ErrorDTO error = new ErrorDTO(
                "METHOD_NOT_ALLOWED",
                String.format("Method %s not supported for %s. Supported methods: %s",
                        e.getMethod(),
                        request.getRequestURI(),
                        e.getSupportedMethods() != null ?
                                String.join(", ", e.getSupportedMethods()) : "none"),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(
            Exception e, HttpServletRequest request) {

        log.error("Unexpected exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String determineFeignErrorMessage(FeignException e) {
        return switch (e.status()) {
            case 404 -> "Person service endpoint not found";
            case 500 -> "Person service internal error";
            case 503 -> "Person service temporarily unavailable";
            case 408, 504 -> "Timeout calling Person service";
            default -> "Error calling Person service: " + e.getMessage();
        };
    }
}