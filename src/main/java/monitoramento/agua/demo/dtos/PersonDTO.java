package monitoramento.agua.demo.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PersonDTO(
    @NotNull
    @NotBlank
    String name, 
    @Email
    @NotNull
    @NotBlank
    String email, 
    @NotNull
    @NotBlank
    String role) {

}
