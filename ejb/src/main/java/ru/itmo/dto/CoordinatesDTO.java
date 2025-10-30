package ru.itmo.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDTO implements Serializable {
    long x;
    long y;
}
