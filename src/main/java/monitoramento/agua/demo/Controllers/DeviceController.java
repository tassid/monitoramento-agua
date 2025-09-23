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
import monitoramento.agua.demo.dtos.DeviceDTO;
import monitoramento.agua.demo.models.Device;
import monitoramento.agua.demo.services.DeviceService;

@RestController
@RequestMapping("/device")
public class DeviceController {
 private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @PostMapping
    public Device create(@RequestBody @Valid DeviceDTO dto) {
        return service.save(dto);
    }

    @GetMapping
    public Page<Device> getDevices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Device updateDevice(@PathVariable String id, @RequestBody @Valid DeviceDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteDevice(@PathVariable String id) {
        return service.delete(id);
    }
}