package ru.itmo.dto;

import ru.itmo.dto.enums.Color;

import java.io.Serializable;

public record HairColorStatsDTO(
        Color hairColor,
        double percentage,
        long totalPersons,
        long personsWithHairColor
) implements Serializable {}