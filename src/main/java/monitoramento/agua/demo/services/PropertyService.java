package monitoramento.agua.demo.services;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import monitoramento.agua.demo.dtos.PropertyDTO;
import monitoramento.agua.demo.exception.NotFoundException;
import monitoramento.agua.demo.models.Person;
import monitoramento.agua.demo.models.Property;
import monitoramento.agua.demo.repositories.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepo;

    @Autowired
    private PersonService personService;

    public Property save(PropertyDTO dto) {

        Person person = personService.getById(dto.personId());

        Property property = new Property();
        BeanUtils.copyProperties(dto, property);
        property.setPerson(person);

        return propertyRepo.save(property);
    }

    public Page<Property> getAll(int page, int size) {
        return propertyRepo.findAll(PageRequest.of(page, size));
    }

    public Property getById(String id) {
        return propertyRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Propriedade com o id: " + id + " não existe"));
    }

    public String delete(String id) {
        propertyRepo.delete(getById(id));
        return "Deletado com sucesso!";
    }

    public Page<Property> listByPerson(String personId, int page, int size) {
        return propertyRepo.findByPersonId(UUID.fromString(personId), PageRequest.of(page, size));
    }

    public Property update(String id, PropertyDTO dto) {
        Property property = getById(id);
        Person person = personService.getById(dto.personId());

        BeanUtils.copyProperties(dto, property, "id");
        property.setPerson(person);

        return propertyRepo.save(property);
    }

}
