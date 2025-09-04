package ru.itmo.demography_service.exception.handler;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.itmo.demography_service.dto.ErrorDTO;
import ru.itmo.demography_service.exception.InvalidParameterException;
import ru.itmo.demography_service.exception.PersonServiceException;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PersonServiceException.class)
    public ResponseEntity<ErrorDTO> handlePersonServiceException(
            PersonServiceException e, HttpServletRequest request) {

        log.error("Person service exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                e.getStatusCode(),
                "EXTERNAL_SERVICE_ERROR",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(e.getStatusCode()).body(error);
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
                correctStatus.value(),
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

        log.warn("Invalid parameter: {}", e.getMessage());

        ErrorDTO error = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PARAMETER",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        log.warn("Method argument type mismatch: parameter={}, value={}, expectedType={}",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                e.getValue(), e.getName(), e.getRequiredType().getSimpleName()
        );

        ErrorDTO error = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PARAMETER_TYPE",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("HTTP message not readable: {}", e.getMessage());

        ErrorDTO error = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST_BODY",
                "Invalid JSON format or request body",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDTO> handleNoHandlerFound(
            NoHandlerFoundException e, HttpServletRequest request) {

        log.warn("No handler found: {}", e.getRequestURL());

        ErrorDTO error = new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "ENDPOINT_NOT_FOUND",
                String.format("No handler found for %s %s", e.getHttpMethod(), e.getRequestURL()),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorDTO> handleTimeoutException(
            TimeoutException e, HttpServletRequest request) {

        log.error("Timeout exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                "TIMEOUT_ERROR",
                "Request timeout while calling external service",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {

        log.warn("Illegal argument: {}", e.getMessage());

        ErrorDTO error = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                e.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(
            Exception e, HttpServletRequest request) {

        log.error("Unexpected exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler({TimeoutException.class, SocketTimeoutException.class})
    public ResponseEntity<ErrorDTO> handleTimeoutException(
            Exception e, HttpServletRequest request) {

        log.error("Timeout exception: {}", e.getMessage(), e);

        ErrorDTO error = new ErrorDTO(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                "TIMEOUT_ERROR",
                "Timeout calling external service",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDTO> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        ErrorDTO error = new ErrorDTO(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "METHOD_NOT_ALLOWED",
                "Method " + e.getMethod() + " not supported for " + request.getRequestURI(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
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

    private HttpStatus mapFeignToCorrectStatus(int feignStatus) {
        return switch (feignStatus) {
            case 404, 500 -> HttpStatus.SERVICE_UNAVAILABLE;
            case 408, 504 -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    }
}