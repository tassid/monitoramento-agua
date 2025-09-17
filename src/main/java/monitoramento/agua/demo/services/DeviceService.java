package monitoramento.agua.demo.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import monitoramento.agua.demo.dtos.DeviceDTO;
import monitoramento.agua.demo.models.Device;
import monitoramento.agua.demo.repositories.DeviceRepository;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepo;

    public Device save(DeviceDTO dto) {

        Device device = new Device();

        BeanUtils.copyProperties(dto, device);
        return deviceRepo.save(device);
    }

}
