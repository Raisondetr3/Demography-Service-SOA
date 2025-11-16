package ru.itmo.controller;

import ru.itmo.dto.HairColorStatsDTO;
import ru.itmo.dto.enums.Color;

import javax.ejb.Stateless;

@Stateless
public class TestService {
    public HairColorStatsDTO calculateHairColorPercentage(Color hairColor) {
        return new HairColorStatsDTO(hairColor, 0.2, 10, 2);
    }
}
