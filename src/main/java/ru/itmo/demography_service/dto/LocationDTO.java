package ru.itmo.demography_service.dto;

public record LocationDTO(
        int x,
        double y,
        double z,
        String name
) {}
