package ru.itmo.config;

import ru.itmo.service.DemographyService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.NotFoundException;
import java.util.Properties;

public class JNDIConfig {
    public static DemographyService demographyService(){
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProps.put("jboss.naming.client.ejb.context", true);
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        try {
            final Context context = new InitialContext(jndiProps);
            return  (DemographyService) context.lookup("ejb:/demography-service-ejb/DemographyService!ru.itmo.service.DemographyServiceRemote");
        } catch (NamingException e){
            System.out.println("JNDIConfig error:");
            e.printStackTrace();
            throw new NotFoundException();
        }
    }
}
