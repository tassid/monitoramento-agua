package monitoramento.agua.demo.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import monitoramento.agua.demo.dtos.PersonDTO;
import monitoramento.agua.demo.models.Person;
import monitoramento.agua.demo.repositories.PersonRepository;

public class PersonService {

    @Autowired
    private PersonRepository personRepo;

    public Person save(String id, PersonDTO dto) {

        // Person person = personRepo.findById(UUID.fromString(id)).
        //         orElseThrow(()
        //                 -> new RuntimeException("Pessoa " + id + " não existe"));
        Person person = new Person();
        BeanUtils.copyProperties(dto, person);
        return personRepo.save(person);
    }

    public List<Person> get(String id, PersonDTO dto) {
        return personRepo.findAll();
    }

    public Person getById(String id) {
        return personRepo.findById(UUID.fromString(id)).orElseThrow(() -> new RuntimeException("Pessoa " + id + " não existe"));
    }

    public void delete(String id) {
        personRepo.delete(getById(id));
    }

    public Person update(String id, PersonDTO dto) {
        var person = getById(id);
        BeanUtils.copyProperties(dto, person, "id");
        return personRepo.save(person);
        //teste
    }
}
