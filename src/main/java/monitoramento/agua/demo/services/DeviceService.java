package monitoramento.agua.demo.services;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import monitoramento.agua.demo.dtos.DeviceDTO;
import monitoramento.agua.demo.exception.NotFoundException;
import monitoramento.agua.demo.models.Device;
import monitoramento.agua.demo.models.Property;
import monitoramento.agua.demo.repositories.DeviceRepository;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private PropertyService propertyService;

    public Device save(DeviceDTO dto) {

        Device device = new Device();
        Property property = propertyService.getById(dto.propertyId());

        BeanUtils.copyProperties(dto, device);
        device.setProperty(property);

        return deviceRepo.save(device);
    }

    public Page<Device> getAll(int page, int size) {
        return deviceRepo.findAll(PageRequest.of(page, size));
    }

    public Device getById(String id) {
        return deviceRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Dispositov com id " + id + " não existe"));
    }

    public String delete(String id) {
        deviceRepo.delete(getById(id));
        return "Deleted successfully!";
    }

    public Device update(String id, DeviceDTO dto) {
        Device device = getById(id);
        Property property = propertyService.getById(dto.propertyId());

        BeanUtils.copyProperties(dto, device, "id");
        device.setProperty(property);

        return deviceRepo.save(device);
    }

}
