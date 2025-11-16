package ru.itmo.config;

import ru.itmo.service.DemographyService;
import ru.itmo.service.DemographyServiceRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.NotFoundException;
import java.util.Properties;

public class JNDIConfig {
    public static DemographyServiceRemote demographyService(){
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        jndiProps.put("jboss.naming.client.ejb.context", true);
        jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
        try {
            final Context context = new InitialContext(jndiProps);
            System.out.println("🔍 JNDI lookup starting...");
//            return  (DemographyService) context.lookup("ejb:/demography-service-ejb/DemographyService!ru.itmo.service.DemographyServiceRemote");

            return  (DemographyServiceRemote) context.lookup("ejb:/ejb/DemographyService!ru.itmo.service.DemographyServiceRemote");
//            return  (DemographyServiceRemote) context.lookup("ejb:/ejb/DemographyService!ru.itmo.service.DemographyServiceRemote");

//            return  (DemographyService) context.lookup("ejb:ejb/ejb//DemographyService!ru.itmo.service.DemographyServiceRemote");
//            System.out.println("✅ Proxy class: " + service.getClass().getName());
        } catch (NamingException e){
            System.out.println("JNDIConfig error:");
            e.printStackTrace();
            throw new NotFoundException();
        }
    }
}
