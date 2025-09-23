package monitoramento.agua.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import monitoramento.agua.demo.models.SensorType;

public record SensorDTO(
        @NotBlank
        String name,
        @NotNull
        SensorType sensorType,
        @NotNull
        String deviceId) {
}
