package monitoramento.agua.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PropertyDTO(
        @NotBlank
        String areaHa,
        @NotBlank
        String address,
        @NotNull
        String personId
        ) {

}
