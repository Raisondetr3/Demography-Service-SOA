package ru.itmo.demography_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itmo.demography_service.config.FeignConfig;
import ru.itmo.demography_service.client.dto.PersonDTO;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "person-service",
        url = "${person-service.url:http://localhost:8080}",
        configuration = FeignConfig.class
)
public interface PersonServiceClient {

    @GetMapping("/persons")
    List<PersonDTO> getAllPersons();

    @GetMapping("/persons/{id}")
    PersonDTO getPersonById(@PathVariable Integer id);

    @GetMapping("/persons/nationality-less-than/{nationality}")
    List<PersonDTO> getPersonsByNationalityLessThan(@PathVariable String nationality);

    @GetMapping("/persons/count")
    Long getPersonsCount();
}