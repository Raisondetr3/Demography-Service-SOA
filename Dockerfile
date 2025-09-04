FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/demography-service-*.jar demography-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "demography-service.jar"]