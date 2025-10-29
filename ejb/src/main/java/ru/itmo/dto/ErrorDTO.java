package ru.itmo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@AllArgsConstructor
public class ErrorDTO implements Serializable {
    private final String error;
    private final String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    private final String path;

    public ErrorDTO(String error, String message, LocalDateTime timestamp) {
        this(error, message, timestamp.atZone(ZoneId.systemDefault()).toInstant(), null);
    }
}