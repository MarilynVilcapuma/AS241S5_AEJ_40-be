package com.example.detectoria_nosql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectRequest {

    @NotBlank(message = "El texto no puede estar vacío")
    @Size(min = 10, max = 5000, message = "El texto debe tener entre 10 y 5000 caracteres")
    private String text;
}
