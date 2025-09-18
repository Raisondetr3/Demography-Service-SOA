package ru.itmo.demography_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.demography_service.dto.HairColorStatsDTO;
import ru.itmo.demography_service.dto.NationalityEyeColorStatsDTO;
import ru.itmo.demography_service.dto.enums.Color;
import ru.itmo.demography_service.dto.enums.Country;
import ru.itmo.demography_service.exception.InvalidParameterException;
import ru.itmo.demography_service.service.DemographyService;

@RestController
@RequestMapping("/demography")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Demography", description = "Demographic analysis of population")
public class DemographyController {

    private final DemographyService demographyService;

    @Operation(
            summary = "Get percentage of people by hair color",
            description = "Calculate percentage ratio of people with specified hair color relative to total population"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid hairColor parameter"),
            @ApiResponse(responseCode = "404", description = "Endpoint not found"),
            @ApiResponse(responseCode = "405", description = "Method not supported"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Person Service unavailable"),
            @ApiResponse(responseCode = "504", description = "Timeout when calling Person Service")
    })
    @GetMapping("/hair-color/{hairColor}/percentage")
    public ResponseEntity<HairColorStatsDTO> getHairColorPercentage(
            @Parameter(description = "Hair color (GREEN, BLUE, ORANGE, BROWN)", required = true)
            @PathVariable String hairColor) {

        log.info("Received request for hair color percentage: {}", hairColor);

        if (hairColor == null || hairColor.trim().isEmpty()) {
            throw new InvalidParameterException("hairColor", hairColor,
                    "Parameter hairColor cannot be empty");
        }

        Color parsedColor = parseColor(hairColor);
        HairColorStatsDTO stats = demographyService.calculateHairColorPercentage(parsedColor);

        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Get statistics by nationality and eye color",
            description = "Count the number of people with specific eye color within specified nationality"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid nationality or eyeColor parameters"),
            @ApiResponse(responseCode = "404", description = "Endpoint not found"),
            @ApiResponse(responseCode = "405", description = "Method not supported"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Person Service unavailable"),
            @ApiResponse(responseCode = "504", description = "Timeout when calling Person Service")
    })
    @GetMapping("/nationality/{nationality}/eye-color/{eyeColor}")
    public ResponseEntity<NationalityEyeColorStatsDTO> getNationalityEyeColorStats(
            @Parameter(description = "Nationality (FRANCE, SPAIN, INDIA, THAILAND, SOUTH_KOREA)", required = true)
            @PathVariable String nationality,
            @Parameter(description = "Eye color (GREEN, BLUE, ORANGE, BROWN)", required = true)
            @PathVariable String eyeColor) {

        log.info("Received request for statistics: {} - {}", nationality, eyeColor);

        if (nationality == null || nationality.trim().isEmpty()) {
            throw new InvalidParameterException("nationality", nationality,
                    "Parameter nationality cannot be empty");
        }
        if (eyeColor == null || eyeColor.trim().isEmpty()) {
            throw new InvalidParameterException("eyeColor", eyeColor,
                    "Parameter eyeColor cannot be empty");
        }

        Country parsedNationality = parseCountry(nationality);
        Color parsedEyeColor = parseColor(eyeColor);

        NationalityEyeColorStatsDTO stats = demographyService
                .calculateNationalityEyeColorStats(parsedNationality, parsedEyeColor);

        return ResponseEntity.ok(stats);
    }

    private Color parseColor(String colorStr) {
        try {
            return Color.valueOf(colorStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("color", colorStr,
                    "Invalid color '" + colorStr + "'. Allowed values: GREEN, BLUE, ORANGE, BROWN");
        } catch (NullPointerException e) {
            throw new InvalidParameterException("color", null, "Color cannot be null");
        }
    }

    private Country parseCountry(String countryStr) {
        try {
            return Country.valueOf(countryStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("nationality", countryStr,
                    "Invalid nationality '" + countryStr + "'. Allowed values: FRANCE, SPAIN, INDIA, THAILAND, SOUTH_KOREA");
        } catch (NullPointerException e) {
            throw new InvalidParameterException("nationality", null, "Nationality cannot be null");
        }
    }
}