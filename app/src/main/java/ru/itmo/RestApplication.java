package ru.itmo;

import ru.itmo.controller.DemographyController;
import ru.itmo.controller.PingController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationPath("/api")
public class RestApplication extends Application {
    private static final Logger log = Logger.getLogger(RestApplication.class.getName());

    public RestApplication() {
        log.info("RESTEasy started! Registered controllers: PingController, DemographyController");
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(PingController.class, DemographyController.class);
    }
}