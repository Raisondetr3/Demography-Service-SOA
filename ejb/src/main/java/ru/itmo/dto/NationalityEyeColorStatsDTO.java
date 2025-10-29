package ru.itmo.dto;

import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;

import java.io.Serializable;

public record NationalityEyeColorStatsDTO(
        Country nationality,
        Color eyeColor,
        long count,
        long totalPersonsInNationality
) implements Serializable {}
