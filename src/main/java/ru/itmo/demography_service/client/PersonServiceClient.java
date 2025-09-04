package ru.itmo.demography_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.demography_service.config.FeignConfig;
import ru.itmo.demography_service.client.dto.PersonDTO;

import java.util.List;

@FeignClient(
        name = "person-service",
        url = "${person-service.url:http://localhost:8080}",
        configuration = FeignConfig.class
)
public interface PersonServiceClient {

    @GetMapping("/api/persons")
    List<PersonDTO> getAllPersons();

    @GetMapping("/api/persons/{id}")
    PersonDTO getPersonById(@PathVariable Integer id);

    @GetMapping("/api/persons/nationality-less-than/{nationality}")
    List<PersonDTO> getPersonsByNationalityLessThan(@PathVariable String nationality);

    @GetMapping("/api/persons/count")
    Long getPersonsCount();
}