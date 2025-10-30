package ru.itmo.dto;

import lombok.*;
import ru.itmo.dto.enums.Color;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HairColorStatsDTO implements Serializable {
    Color hairColor;
    double percentage;
    long totalPersons;
    long personsWithHairColor;
}