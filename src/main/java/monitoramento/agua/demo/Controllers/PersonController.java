package monitoramento.agua.demo.Controllers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import monitoramento.agua.demo.dtos.PersonDTO;
import monitoramento.agua.demo.models.Person;
import monitoramento.agua.demo.services.PersonService;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping()
    public Person create(@RequestBody PersonDTO dto) {
        return service.save(dto);
    }

    @GetMapping()
    public Page<Person> getPerson(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Person putMethodName(@PathVariable String id, @RequestBody PersonDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable String id) {
        return service.delete(id);
    }

}
