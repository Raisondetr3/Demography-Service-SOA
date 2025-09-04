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
@Tag(name = "Demography", description = "Демографический анализ популяции")
public class DemographyController {

    private final DemographyService demographyService;

    @Operation(
            summary = "Получить процент людей по цвету волос",
            description = "Вычисляет процентное соотношение людей с указанным цветом волос относительно общей популяции"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика успешно рассчитана"),
            @ApiResponse(responseCode = "400", description = "Некорректный параметр hairColor"),
            @ApiResponse(responseCode = "404", description = "Endpoint не найден"),
            @ApiResponse(responseCode = "405", description = "Метод не поддерживается"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "503", description = "Person Service недоступен"),
            @ApiResponse(responseCode = "504", description = "Таймаут при обращении к Person Service")
    })
    @GetMapping("/hair-color/{hairColor}/percentage")
    public ResponseEntity<HairColorStatsDTO> getHairColorPercentage(
            @Parameter(description = "Цвет волос (GREEN, BLUE, ORANGE, BROWN)", required = true)
            @PathVariable String hairColor) {

        log.info("Получен запрос на процент по цвету волос: {}", hairColor);

        if (hairColor == null || hairColor.trim().isEmpty()) {
            throw new InvalidParameterException("hairColor", hairColor,
                    "Параметр hairColor не может быть пустым");
        }

        Color parsedColor = parseColor(hairColor);
        HairColorStatsDTO stats = demographyService.calculateHairColorPercentage(parsedColor);

        return ResponseEntity.ok(stats);
    }

    @Operation(
            summary = "Получить статистику по национальности и цвету глаз",
            description = "Подсчитывает количество людей с определенным цветом глаз в рамках указанной национальности"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статистика успешно рассчитана"),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры nationality или eyeColor"),
            @ApiResponse(responseCode = "404", description = "Endpoint не найден"),
            @ApiResponse(responseCode = "405", description = "Метод не поддерживается"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "503", description = "Person Service недоступен"),
            @ApiResponse(responseCode = "504", description = "Таймаут при обращении к Person Service")
    })
    @GetMapping("/nationality/{nationality}/eye-color/{eyeColor}")
    public ResponseEntity<NationalityEyeColorStatsDTO> getNationalityEyeColorStats(
            @Parameter(description = "Национальность (FRANCE, SPAIN, INDIA, THAILAND, SOUTH_KOREA)", required = true)
            @PathVariable String nationality,
            @Parameter(description = "Цвет глаз (GREEN, BLUE, ORANGE, BROWN)", required = true)
            @PathVariable String eyeColor) {

        log.info("Получен запрос на статистику: {} - {}", nationality, eyeColor);

        if (nationality == null || nationality.trim().isEmpty()) {
            throw new InvalidParameterException("nationality", nationality,
                    "Параметр nationality не может быть пустым");
        }
        if (eyeColor == null || eyeColor.trim().isEmpty()) {
            throw new InvalidParameterException("eyeColor", eyeColor,
                    "Параметр eyeColor не может быть пустым");
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
                    "Недопустимый цвет '" + colorStr + "'. Разрешенные значения: GREEN, BLUE, ORANGE, BROWN");
        } catch (NullPointerException e) {
            throw new InvalidParameterException("color", null, "Цвет не может быть null");
        }
    }

    private Country parseCountry(String countryStr) {
        try {
            return Country.valueOf(countryStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("nationality", countryStr,
                    "Недопустимая национальность '" + countryStr + "'. Разрешенные значения: FRANCE, SPAIN, INDIA, THAILAND, SOUTH_KOREA");
        } catch (NullPointerException e) {
            throw new InvalidParameterException("nationality", null, "Национальность не может быть null");
        }
    }
}