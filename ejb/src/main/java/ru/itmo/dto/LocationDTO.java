package ru.itmo.dto;

import java.io.Serializable;

public record LocationDTO(
        int x,
        double y,
        double z,
        String name
) implements Serializable {}
