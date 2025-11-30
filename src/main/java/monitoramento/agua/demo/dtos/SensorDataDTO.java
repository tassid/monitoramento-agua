package monitoramento.agua.demo.dtos;

import jakarta.validation.constraints.NotBlank;

public record SensorDataDTO(
        @NotBlank
        String type,
        @NotBlank
        double value,
        @NotBlank
        String message,
        @NotBlank
        String timestamp
        ) {

}
