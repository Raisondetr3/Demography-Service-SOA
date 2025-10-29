package ru.itmo.controller;


import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ru.itmo.dto.HairColorStatsDTO;
import ru.itmo.dto.NationalityEyeColorStatsDTO;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;
import ru.itmo.service.DemographyServiceRemote;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/demography")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Demography", description = "Demographic analysis of population")
public class DemographyController {
    private static final Logger log = Logger.getLogger(DemographyController.class.getName());

    @EJB
    private DemographyServiceRemote demographyService;

    @GET
    @Path("/test")
    public Response test() {

        log.info("Test test: " );

        return Response.ok("test ok").build();
    }

    @Operation(
            summary = "Get percentage of people by hair color",
            description = "Calculate percentage ratio of people with specified hair color relative to total population"
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Statistics calculated successfully",
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
            @APIResponse(responseCode = "400", description = "Invalid hair color parameter",
                    content = @Content(mediaType = "application/json"))
    })
    @GET
    @Path("/hair-color/{hairColor}/percentage")
    public Response getHairColorPercentage(
            @Parameter(description = "Hair color", required = true,
                    schema = @Schema(implementation = Color.class))
            @PathParam("hairColor") Color hairColor) {

        log.info("Received request for hair color percentage: " + hairColor);

        HairColorStatsDTO stats = demographyService.calculateHairColorPercentage(hairColor);
        return Response.ok(stats).build();
    }

    @Operation(
            summary = "Get statistics by nationality and eye color",
            description = "Count the number of people with specific eye color within specified nationality"
    )
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Statistics calculated successfully",
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
            @APIResponse(responseCode = "400", description = "Invalid nationality or eye color parameters",
                    content = @Content(mediaType = "application/json"))
    })
    @GET
    @Path("/nationality/{nationality}/eye-color/{eyeColor}")
    public Response getNationalityEyeColorStats(
            @Parameter(description = "Nationality", required = true,
                    schema = @Schema(implementation = Country.class))
            @PathParam("nationality") Country nationality,

            @Parameter(description = "Eye color", required = true,
                    schema = @Schema(implementation = Color.class))
            @PathParam("eyeColor") Color eyeColor) {

        log.info("Received request for statistics: " + nationality + " - " + eyeColor);

        NationalityEyeColorStatsDTO stats = demographyService
                .calculateNationalityEyeColorStats(nationality, eyeColor);

        return Response.ok(stats).build();
    }
}