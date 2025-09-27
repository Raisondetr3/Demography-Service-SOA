package ru.itmo.demography_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmo.demography_service.dto.ErrorDTO;
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
            @ApiResponse(responseCode = "200", description = "Statistics calculated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = HairColorStatsDTO.class),
                            examples = @ExampleObject(
                                    name = "Hair Color Statistics",
                                    value = """
                                    {
                                        "hairColor": "BLUE",
                                        "percentage": 23.5,
                                        "totalCount": 100,
                                        "colorCount": 23
                                    }
                                    """
                            ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid hair color parameter",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid Enum Value",
                                            description = "Hair color value is not valid",
                                            value = """
                                            {
                                                "error": "INVALID_PARAMETER_TYPE",
                                                "message": "Invalid value 'PURPLE' for parameter 'hairColor'. Expected one of: [GREEN, BLUE, ORANGE, BROWN]",
                                                "timestamp": "2025-09-19T09:32:19.479Z",
                                                "path": "/demography/hair-color/PURPLE/percentage"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Empty Parameter",
                                            description = "Hair color parameter is empty",
                                            value = """
                                            {
                                                "error": "INVALID_REQUEST_PARAMETER",
                                                "message": "Parameter hairColor cannot be empty",
                                                "timestamp": "2025-09-19T09:32:19.479Z",
                                                "path": "/demography/hair-color//percentage"
                                            }
                                            """
                                    )
                            })
            )
    })
    @GetMapping("/hair-color/{hairColor}/percentage")
    public ResponseEntity<HairColorStatsDTO> getHairColorPercentage(
            @Parameter(description = "Hair color", required = true,
                    schema = @Schema(implementation = Color.class,
                            allowableValues = {"GREEN", "BLUE", "ORANGE", "BROWN"}))
            @PathVariable Color hairColor) {

        log.info("Received request for hair color percentage: {}", hairColor);

        HairColorStatsDTO stats = demographyService.calculateHairColorPercentage(hairColor);
        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Get statistics by nationality and eye color",
            description = "Count the number of people with specific eye color within specified nationality"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics calculated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NationalityEyeColorStatsDTO.class),
                            examples = @ExampleObject(
                                    name = "Nationality Eye Color Statistics",
                                    value = """
                                    {
                                        "nationality": "SPAIN",
                                        "eyeColor": "GREEN",
                                        "eyeColorCount": 15,
                                        "totalNationalityCount": 45
                                    }
                                    """
                            ))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid nationality or eye color parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid Nationality",
                                            description = "Nationality value is not valid",
                                            value = """
                                            {
                                                "error": "INVALID_PARAMETER_TYPE",
                                                "message": "Invalid value 'ATLANTIS' for parameter 'nationality'. Expected one of: [SPAIN, INDIA, VATICAN, SOUTH_KOREA, JAPAN]",
                                                "timestamp": "2025-09-19T09:32:19.479Z",
                                                "path": "/demography/nationality/ATLANTIS/eye-color/GREEN"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Eye Color",
                                            description = "Eye color value is not valid",
                                            value = """
                                            {
                                                "error": "INVALID_PARAMETER_TYPE",
                                                "message": "Invalid value 'PURPLE' for parameter 'eyeColor'. Expected one of: [GREEN, BLUE, ORANGE, BROWN]",
                                                "timestamp": "2025-09-19T09:32:19.479Z",
                                                "path": "/demography/nationality/SPAIN/eye-color/PURPLE"
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Multiple Invalid Parameters",
                                            description = "Both parameters are invalid",
                                            value = """
                                            {
                                                "error": "INVALID_PARAMETER_TYPE",
                                                "message": "Invalid value 'ABC' for parameter 'nationality'. Expected one of: [SPAIN, INDIA, VATICAN, SOUTH_KOREA, JAPAN]",
                                                "timestamp": "2025-09-19T09:32:19.479Z",
                                                "path": "/demography/nationality/ABC/eye-color/XYZ"
                                            }
                                            """
                                    )
                            })
            )
    })
    @GetMapping("/nationality/{nationality}/eye-color/{eyeColor}")
    public ResponseEntity<NationalityEyeColorStatsDTO> getNationalityEyeColorStats(
            @Parameter(description = "Nationality", required = true,
                    schema = @Schema(implementation = Country.class,
                            allowableValues = {"SPAIN", "INDIA", "VATICAN", "SOUTH_KOREA", "JAPAN"}))
            @PathVariable Country nationality,
            @Parameter(description = "Eye color", required = true,
                    schema = @Schema(implementation = Color.class,
                            allowableValues = {"GREEN", "BLUE", "ORANGE", "BROWN"}))
            @PathVariable Color eyeColor) {

        log.info("Received request for statistics: {} - {}", nationality, eyeColor);

        NationalityEyeColorStatsDTO stats = demographyService
                .calculateNationalityEyeColorStats(nationality, eyeColor);

        return ResponseEntity.ok(stats);
    }
}