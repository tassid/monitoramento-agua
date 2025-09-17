package monitoramento.agua.demo.services;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import monitoramento.agua.demo.dtos.PropertyDTO;
import monitoramento.agua.demo.models.Property;
import monitoramento.agua.demo.repositories.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepo;

    public Property save(PropertyDTO dto) {
        Property property = new Property();
        BeanUtils.copyProperties(dto, property);
        return propertyRepo.save(property);
    }

    public Page<Property> listByPerson(String personId, int page, int size) {
        return propertyRepo.findByPersonId(UUID.fromString(personId), PageRequest.of(page, size));
    }

}
