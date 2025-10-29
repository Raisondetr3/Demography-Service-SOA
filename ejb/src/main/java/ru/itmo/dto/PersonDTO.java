package ru.itmo.dto;

import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;

import java.io.Serializable;
import java.time.LocalDateTime;

public record PersonDTO(
        Integer id,
        String name,
        CoordinatesDTO coordinates,
        LocalDateTime creationDate,
        Long height,
        Float weight,
        Color hairColor,
        Color eyeColor,
        Country nationality,
        LocationDTO location
) implements Serializable {}