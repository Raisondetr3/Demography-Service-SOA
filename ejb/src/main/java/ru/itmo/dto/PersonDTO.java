package ru.itmo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO implements Serializable {
    Integer id;
    String name;
    CoordinatesDTO coordinates;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    LocalDateTime creationDate;

    Long height;
    Float weight;
    Color hairColor;
    Color eyeColor;
    Country nationality;
    LocationDTO location;
}