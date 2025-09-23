package monitoramento.agua.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import monitoramento.agua.demo.models.Status;

public record DeviceDTO(
        @NotBlank
        String name,
        @NotNull
        Status status,
        @NotBlank
        String temperature,
        @NotBlank
        String phProbe,
        @NotBlank
        String turbidity,
        @NotNull
        String propertyId
        ) {

}
