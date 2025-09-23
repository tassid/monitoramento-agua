package monitoramento.agua.demo.services;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import monitoramento.agua.demo.dtos.SensorDTO;
import monitoramento.agua.demo.exception.NotFoundException;
import monitoramento.agua.demo.models.Device;
import monitoramento.agua.demo.models.Sensor;
import monitoramento.agua.demo.repositories.DeviceRepository;
import monitoramento.agua.demo.repositories.SensorRepository;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepo;
    
    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private DeviceService deviceService; // Injetado para buscar o dispositivo

    public Sensor save(SensorDTO dto) {
        Device device = deviceService.getById(dto.deviceId());

        Sensor sensor = new Sensor();
        BeanUtils.copyProperties(dto, sensor);
        sensor.setDevice(device);

        return sensorRepo.save(sensor);
    }

    public Page<Sensor> getAll(int page, int size) {
        return sensorRepo.findAll(PageRequest.of(page, size));
    }

    public Sensor getById(String id) {
        return sensorRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Sensor com id: " + id + " não existe"));
    }

    @Transactional
    public String delete(String id) {
        UUID deviceId = UUID.fromString(id);

        if (!deviceRepo.existsById(deviceId)) {
            throw new NotFoundException("Device with id " + id + " does not exist");
        }

        sensorRepo.deleteAllByDeviceId(deviceId);

        deviceRepo.deleteById(deviceId);

        sensorRepo.delete(getById(id));
        return "Deletado com sucesso!";
    }

    public Sensor update(String id, SensorDTO dto) {
        Sensor sensor = getById(id);
        Device device = deviceService.getById(dto.deviceId());

        BeanUtils.copyProperties(dto, sensor, "id");
        sensor.setDevice(device);

        return sensorRepo.save(sensor);
    }
}
