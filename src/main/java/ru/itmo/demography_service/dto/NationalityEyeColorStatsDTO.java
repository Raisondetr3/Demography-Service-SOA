package ru.itmo.demography_service.dto;

import ru.itmo.demography_service.dto.enums.Color;
import ru.itmo.demography_service.dto.enums.Country;

public record NationalityEyeColorStatsDTO(
        Country nationality,
        Color eyeColor,
        long count,
        long totalPersonsInNationality
) {}
