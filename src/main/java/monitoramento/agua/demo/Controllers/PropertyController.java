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

import jakarta.validation.Valid;
import monitoramento.agua.demo.dtos.PropertyDTO;
import monitoramento.agua.demo.models.Property;
import monitoramento.agua.demo.services.PropertyService;

@RestController
@RequestMapping("/property")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @PostMapping
    public Property create(@RequestBody @Valid PropertyDTO dto) {
        return service.save(dto);
    }

    @GetMapping
    public Page<Property> getProperties(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Property getPropertyById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Property updateProperty(@PathVariable String id, @RequestBody @Valid PropertyDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteProperty(@PathVariable String id) {
        return service.delete(id);
    }
}
