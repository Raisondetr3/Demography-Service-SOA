package ru.itmo.demography_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.demography_service.config.FeignConfig;
import ru.itmo.demography_service.dto.PersonDTO;

import java.util.List;

@FeignClient(
        name = "person-service",
        url = "${person-service.url:http://localhost:58123}",
        configuration = FeignConfig.class
)
public interface PersonServiceClient {

    @GetMapping("/persons?page=0&size=1000000000")
    List<PersonDTO> getAllPersons();

    @GetMapping("/persons/{id}")
    PersonDTO getPersonById(@PathVariable Integer id);

    @GetMapping("/persons/nationality-less-than/{nationality}")
    List<PersonDTO> getPersonsByNationalityLessThan(@PathVariable String nationality);

    @GetMapping("/persons/count")
    Long getPersonsCount();
}