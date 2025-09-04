package ru.itmo.demography_service.client.dto;

import ru.itmo.demography_service.dto.CoordinatesDTO;
import ru.itmo.demography_service.dto.LocationDTO;
import ru.itmo.demography_service.dto.enums.Color;
import ru.itmo.demography_service.dto.enums.Country;

import java.time.LocalDateTime;

public record PersonDTO(
        int id,
        String name,
        CoordinatesDTO coordinates,
        LocalDateTime creationDate,
        Long height,
        float weight,
        Color hairColor,
        Country nationality,
        LocationDTO location
) {}