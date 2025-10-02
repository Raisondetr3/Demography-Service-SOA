package ru.itmo.demography_service.config;

import feign.Client;
import feign.Logger;
import feign.Request;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(
                5000,
                10000,
                true
        );
    }

    @Bean
    public Client feignClient() {
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            return new Client.Default(
                    socketFactory,
                    NoopHostnameVerifier.INSTANCE
            );
        } catch (Exception e) {
            throw new RuntimeException("Error config SSL for Feign Client", e);
        }
    }
}