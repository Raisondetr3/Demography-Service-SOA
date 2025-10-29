import ru.itmo.controller.DemographyController;
import ru.itmo.controller.PingController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/api")
public class RestApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(PingController.class, DemographyController.class);
    }
}