package ru.itmo.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO implements Serializable {
    int x;
    double y;
    double z;
    String name;
}
