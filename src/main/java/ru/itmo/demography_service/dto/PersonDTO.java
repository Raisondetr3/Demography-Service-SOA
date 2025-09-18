package ru.itmo.demography_service.dto;

import ru.itmo.demography_service.dto.CoordinatesDTO;
import ru.itmo.demography_service.dto.LocationDTO;
import ru.itmo.demography_service.dto.enums.Color;
import ru.itmo.demography_service.dto.enums.Country;

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
) {}