package monitoramento.agua.demo.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import monitoramento.agua.demo.dtos.PersonDTO;
import monitoramento.agua.demo.models.Person;
import monitoramento.agua.demo.repositories.PersonRepository;

public class PersonService {

    @Autowired
    private PersonRepository personRepo;

    public Person save(String id, PersonDTO dto) {

        Person person = personRepo.findById(UUID.fromString(id)).
                orElseThrow(()
                        -> new RuntimeException("Pessoa " + id + " não existe"));

        return null;
    }

}
