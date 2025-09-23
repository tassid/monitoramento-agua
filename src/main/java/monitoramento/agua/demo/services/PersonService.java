package monitoramento.agua.demo.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import monitoramento.agua.demo.dtos.PersonDTO;
import monitoramento.agua.demo.exception.ConflictException;
import monitoramento.agua.demo.exception.NotFoundException;
import monitoramento.agua.demo.models.Person;
import monitoramento.agua.demo.repositories.PersonRepository;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepo;

    public Person save(PersonDTO dto) {

        Optional<Person> personOpt = personRepo.findByEmail(dto.email());

        if (personOpt.isPresent()) {
            throw new ConflictException("Email já existe");
        }

        Person person = new Person();
        BeanUtils.copyProperties(dto, person);
        return personRepo.save(person);
    }

    public Page<Person> getAll(int page, int size) {
        return personRepo.findAll(PageRequest.of(page, size));
    }

    public Person getById(String id) {
        return personRepo.findById(UUID.fromString(id)).orElseThrow(() -> new NotFoundException("Pessoa com id " + id + " não existe"));
    }

    public Person findByEmail(String email) {
        return personRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Pessoa com email:" + email + "não encontrada"));
    }

    public String delete(String id) {
        personRepo.delete(getById(id));
        String message = "Deletado com sucesso.";
        return message;
    }

    public Person update(String id, PersonDTO dto) {
        var person = getById(id);
        BeanUtils.copyProperties(dto, person, "id");
        return personRepo.save(person);
    }
}
