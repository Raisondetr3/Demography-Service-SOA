package ru.itmo.demography_service.exception;

public class InvalidParameterException extends RuntimeException {
    private final String parameterName;
    private final Object parameterValue;

    public InvalidParameterException(String parameterName, Object parameterValue, String message) {
        super(message);
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public Object getParameterValue() {
        return parameterValue;
    }
}