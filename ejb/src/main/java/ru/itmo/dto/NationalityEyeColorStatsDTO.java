package ru.itmo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationalityEyeColorStatsDTO implements Serializable {
    Country nationality;
    Color eyeColor;
    long count;
    long totalPersonsInNationality;
}
