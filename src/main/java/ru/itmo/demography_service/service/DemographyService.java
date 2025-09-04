package ru.itmo.demography_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.demography_service.client.PersonServiceClient;
import ru.itmo.demography_service.client.dto.PersonDTO;
import ru.itmo.demography_service.dto.HairColorStatsDTO;
import ru.itmo.demography_service.dto.NationalityEyeColorStatsDTO;
import ru.itmo.demography_service.dto.enums.Color;
import ru.itmo.demography_service.dto.enums.Country;
import ru.itmo.demography_service.exception.InvalidParameterException;
import ru.itmo.demography_service.exception.PersonServiceException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemographyService {

    private final PersonServiceClient personServiceClient;

    public HairColorStatsDTO calculateHairColorPercentage(Color hairColor) {
        validateHairColor(hairColor);

        try {
            log.info("Calculating hair color percentage for: {}", hairColor);

            List<PersonDTO> allPersons = fetchAllPersons();

            if (allPersons.isEmpty()) {
                log.info("No persons found in the system");
                return new HairColorStatsDTO(hairColor, 0.0, 0L, 0L);
            }

            long totalCount = allPersons.size();

            long colorCount = allPersons.stream()
                    .filter(person -> Objects.equals(hairColor, person.hairColor()))
                    .count();

            double percentage = (colorCount * 100.0) / totalCount;

            log.info("Hair color stats: total={}, with_color={}, percentage={}",
                    totalCount, colorCount, percentage);

            return new HairColorStatsDTO(hairColor, percentage, totalCount, colorCount);

        } catch (PersonServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calculating hair color percentage for {}", hairColor, e);
            throw new PersonServiceException(
                    "Failed to calculate hair color statistics: " + e.getMessage(), e);
        }
    }

    public NationalityEyeColorStatsDTO calculateNationalityEyeColorStats(
            Country nationality, Color eyeColor) {

        validateNationality(nationality);
        validateEyeColor(eyeColor);

        try {
            log.info("Calculating nationality eye color stats for: {} - {}", nationality, eyeColor);

            List<PersonDTO> allPersons = fetchAllPersons();

            List<PersonDTO> nationalityPersons = allPersons.stream()
                    .filter(person -> Objects.equals(nationality, person.nationality()))
                    .toList();

            if (nationalityPersons.isEmpty()) {
                log.info("No persons found with nationality: {}", nationality);
                return new NationalityEyeColorStatsDTO(nationality, eyeColor, 0L, 0L);
            }

            long eyeColorCount = nationalityPersons.stream()
                    .filter(person -> Objects.equals(eyeColor, person.hairColor()))
                    .count();

            long totalNationalityCount = nationalityPersons.size();

            log.info("Nationality eye color stats: nationality={}, total={}, with_eye_color={}",
                    nationality, totalNationalityCount, eyeColorCount);

            return new NationalityEyeColorStatsDTO(
                    nationality,
                    eyeColor,
                    eyeColorCount,
                    totalNationalityCount
            );

        } catch (PersonServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calculating nationality eye color stats for {} - {}",
                    nationality, eyeColor, e);
            throw new PersonServiceException(
                    "Failed to calculate nationality eye color statistics: " + e.getMessage(), e);
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
                    .filter(person -> person.hairColor() != null &&
                            person.hairColor().equals(hairColor))
                    .count();

            long colorCountDetailed = allPersons.stream()
                    .mapToLong(person -> {
                        if (person.hairColor() == null) {
                            log.warn("Found person with null hair color: id={}", person.id());
                            return 0L;
                        }
                        return person.hairColor().equals(hairColor) ? 1L : 0L;
                    })
                    .sum();

            double percentage = (colorCount * 100.0) / allPersons.size();

            return new HairColorStatsDTO(hairColor, percentage, allPersons.size(), colorCount);

        } catch (Exception e) {
            log.error("Error in detailed hair color calculation for {}", hairColor, e);
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

    private List<PersonDTO> fetchAllPersons() {
        try {
            List<PersonDTO> persons = personServiceClient.getAllPersons();

            if (persons == null) {
                log.warn("Person service returned null instead of empty list");
                throw new PersonServiceException("Person service returned invalid data");
            }

            long nullNationalityCount = persons.stream()
                    .filter(person -> person.nationality() == null)
                    .count();

            long nullHairColorCount = persons.stream()
                    .filter(person -> person.hairColor() == null)
                    .count();

            if (nullNationalityCount > 0 || nullHairColorCount > 0) {
                log.warn("Found {} persons with null nationality and {} with null hair color",
                        nullNationalityCount, nullHairColorCount);
            }

            return persons;

        } catch (Exception e) {
            log.error("Failed to fetch persons from Person Service", e);
            throw new PersonServiceException(
                    "Unable to retrieve person data from external service", e);
        }
    }
}