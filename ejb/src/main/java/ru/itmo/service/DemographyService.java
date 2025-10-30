package ru.itmo.service;

import ru.itmo.client.PersonServiceClient;
import ru.itmo.dto.*;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;
import ru.itmo.exception.InvalidParameterException;
import ru.itmo.exception.PersonServiceException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class DemographyService implements DemographyServiceRemote {

    private static final Logger log = Logger.getLogger(DemographyService.class.getName());

//    @Inject
    private final PersonServiceClient personServiceClient = new PersonServiceClient();

    public HairColorStatsDTO calculateHairColorPercentage(Color hairColor) {
        validateHairColor(hairColor);

        try {
            log.info("Calculating hair color percentage for: " + hairColor);

            List<PersonDTO> allPersons = fetchAllPersons();

            if (allPersons.isEmpty()) {
                log.info("No persons found in the system");
                return new HairColorStatsDTO(hairColor, 0.0, 0L, 0L);
            }

            long totalCount = allPersons.size();
            long colorCount = allPersons.stream()
                    .filter(person -> Objects.equals(hairColor, person.getHairColor()))
                    .count();

            double percentage = (colorCount * 100.0) / totalCount;

            log.info(String.format("Hair color stats: total=%d, with_color=%d, percentage=%.2f",
                    totalCount, colorCount, percentage));

            return new HairColorStatsDTO(hairColor, percentage, totalCount, colorCount);

        } catch (PersonServiceException e) {
            throw e;
        } catch (Exception e) {
            log.severe("Unexpected error calculating hair color percentage for " + hairColor + ": " + e.getMessage());
            throw new PersonServiceException("Failed to calculate hair color statistics: " + e.getMessage(), e);
        }
    }

    public NationalityEyeColorStatsDTO calculateNationalityEyeColorStats(
            Country nationality, Color eyeColor) {

        validateNationality(nationality);
        validateEyeColor(eyeColor);

        try {
            log.info("Calculating nationality eye color stats for: " + nationality + " - " + eyeColor);

            List<PersonDTO> allPersons = fetchAllPersons();

            List<PersonDTO> nationalityPersons = allPersons.stream()
                    .filter(person -> Objects.equals(nationality, person.getNationality()))
                    .collect(Collectors.toList());

            if (nationalityPersons.isEmpty()) {
                log.info("No persons found with nationality: " + nationality);
                return new NationalityEyeColorStatsDTO(nationality, eyeColor, 0L, 0L);
            }

            long eyeColorCount = nationalityPersons.stream()
                    .filter(person -> Objects.equals(eyeColor, person.getEyeColor()))
                    .count();

            long totalNationalityCount = nationalityPersons.size();

            log.info(String.format("Nationality eye color stats: nationality=%s, total=%d, with_eye_color=%d",
                    nationality, totalNationalityCount, eyeColorCount));

            return new NationalityEyeColorStatsDTO(
                    nationality,
                    eyeColor,
                    eyeColorCount,
                    totalNationalityCount
            );

        } catch (PersonServiceException e) {
            throw e;
        } catch (Exception e) {
            log.severe("Unexpected error calculating nationality eye color stats for " + nationality + " - " + eyeColor + ": " + e.getMessage());
            throw new PersonServiceException("Failed to calculate nationality eye color statistics: " + e.getMessage(), e);
        }
    }

    public HairColorStatsDTO calculateHairColorPercentageWithExplicitValidation(Color hairColor) {
        validateHairColor(hairColor);

        try {
            List<PersonDTO> allPersons = fetchAllPersons();

            if (allPersons.isEmpty()) {
                return new HairColorStatsDTO(hairColor, 0.0, 0L, 0L);
            }

            long colorCount = allPersons.stream()
                    .filter(person -> person.getHairColor() != null &&
                            person.getHairColor().equals(hairColor))
                    .count();

            allPersons.stream()
                    .filter(person -> person.getHairColor() == null)
                    .forEach(person -> log.warning("Found person with null hair color: id=" + person.getId()));

            double percentage = (colorCount * 100.0) / allPersons.size();

            return new HairColorStatsDTO(hairColor, percentage, allPersons.size(), colorCount);

        } catch (Exception e) {
            log.severe("Error in detailed hair color calculation for " + hairColor + ": " + e.getMessage());
            throw new PersonServiceException("Failed to calculate hair color statistics", e);
        }
    }

    private void validateHairColor(Color hairColor) {
        if (hairColor == null) {
            throw new InvalidParameterException("hairColor", null, "Hair color cannot be null");
        }
    }

    private void validateNationality(Country nationality) {
        if (nationality == null) {
            throw new InvalidParameterException("nationality", null, "Nationality cannot be null");
        }
    }

    private void validateEyeColor(Color eyeColor) {
        if (eyeColor == null) {
            throw new InvalidParameterException("eyeColor", null, "Eye color cannot be null");
        }
    }

    // --- Получение данных ---
    private List<PersonDTO> fetchAllPersons() {
        try {
            List<PersonDTO> persons = personServiceClient.getAllPersons();

            if (persons == null) {
                log.warning("Person service returned null instead of empty list");
                throw new PersonServiceException("Person service returned invalid data");
            }

            long nullNationalityCount = persons.stream()
                    .filter(person -> person.getNationality() == null)
                    .count();
            long nullHairColorCount = persons.stream()
                    .filter(person -> person.getHairColor() == null)
                    .count();
            long nullEyeColorCount = persons.stream()
                    .filter(person -> person.getEyeColor() == null)
                    .count();

            if (nullNationalityCount > 0 || nullHairColorCount > 0 || nullEyeColorCount > 0) {
                log.warning(String.format(
                        "Found %d persons with null nationality, %d with null hair color, and %d with null eye color",
                        nullNationalityCount, nullHairColorCount, nullEyeColorCount));
            }

            return persons;

        } catch (Exception e) {
            log.severe("Failed to fetch persons from Person Service: " + e.getMessage());
            throw new PersonServiceException("Unable to retrieve person data from external service", e);
        }
    }
}