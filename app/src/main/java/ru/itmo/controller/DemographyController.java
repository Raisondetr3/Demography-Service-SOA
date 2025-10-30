package ru.itmo.controller;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ru.itmo.dto.HairColorStatsDTO;
import ru.itmo.dto.NationalityEyeColorStatsDTO;
import ru.itmo.dto.enums.Color;
import ru.itmo.dto.enums.Country;
import ru.itmo.service.DemographyService;
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

//    @EJB
    private final DemographyServiceRemote demographyService = new DemographyService();

    @GET
    @Path("/test")
    public Response test() {

        log.info("Test test: " );

        return Response.ok("test ok").build();
    }

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