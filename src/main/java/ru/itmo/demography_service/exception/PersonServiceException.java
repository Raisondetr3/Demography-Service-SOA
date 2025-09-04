package ru.itmo.demography_service.exception;

public class PersonServiceException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    public PersonServiceException(String message) {
        super(message);
        this.statusCode = 503;
        this.errorCode = "PERSON_SERVICE_ERROR";
    }

    public PersonServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 503;
        this.errorCode = "PERSON_SERVICE_ERROR";
    }

    public PersonServiceException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() { return statusCode; }
    public String getErrorCode() { return errorCode; }
}