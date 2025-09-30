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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import monitoramento.agua.demo.dtos.DeviceDTO;
import monitoramento.agua.demo.models.Device;
import monitoramento.agua.demo.services.DeviceService;

@RestController
@RequestMapping("/device")
@Tag(name = "Device", description = "Endpoints para gerenciamento de Dispositivos")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cria um novo dispositivo", description = "Registra um novo dispositivo no sistema associado a uma propriedade.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dispositivo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public Device create(@RequestBody @Valid DeviceDTO dto) {
        return service.save(dto);
    }

    @GetMapping
    @Operation(summary = "Lista todos os dispositivos de forma paginada")

    public Page<Device> getDevices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.getAll(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um dispositivo por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dispositivo encontrado"),
        @ApiResponse(responseCode = "404", description = "Dispositivo não encontrado com o ID fornecido")
    })
    public Device getDeviceById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um dispositivo existente")
    public Device updateDevice(@PathVariable String id, @RequestBody @Valid DeviceDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um dispositivo por ID")
    public String deleteDevice(@PathVariable String id) {
        return service.delete(id);
    }
}
