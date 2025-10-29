package ru.itmo.dto;

import java.io.Serializable;

public record CoordinatesDTO(
        long x,
        long y
) implements Serializable {}
