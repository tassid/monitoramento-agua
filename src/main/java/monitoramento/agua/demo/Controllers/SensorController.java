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
import monitoramento.agua.demo.dtos.SensorDTO;
import monitoramento.agua.demo.models.Sensor;
import monitoramento.agua.demo.services.SensorService;


@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    @PostMapping
    public Sensor create(@RequestBody @Valid SensorDTO dto) {
        return service.save(dto);
    }

    @GetMapping
    public Page<Sensor> getSensors(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Sensor getSensorById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Sensor updateSensor(@PathVariable String id, @RequestBody @Valid SensorDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteSensor(@PathVariable String id) {
        return service.delete(id);
    }
}
