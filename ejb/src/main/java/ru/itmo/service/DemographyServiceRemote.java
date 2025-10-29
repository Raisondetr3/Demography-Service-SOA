package ru.itmo.service;

import ru.itmo.dto.HairColorStatsDTO;
import ru.itmo.dto.NationalityEyeColorStatsDTO;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;

import javax.ejb.Remote;
import java.io.Serializable;

@Remote
public interface DemographyServiceRemote extends Serializable {

    HairColorStatsDTO calculateHairColorPercentage(Color hairColor);

    NationalityEyeColorStatsDTO calculateNationalityEyeColorStats(Country nationality, Color eyeColor);

    HairColorStatsDTO calculateHairColorPercentageWithExplicitValidation(Color hairColor);
}