package ru.itmo.demography_service.dto;

import ru.itmo.demography_service.dto.enums.Color;

public record HairColorStatsDTO(
        Color hairColor,
        double percentage,
        long totalPersons,
        long personsWithHairColor
) {}