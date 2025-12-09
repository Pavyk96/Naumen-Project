package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO-запрос для индустрии
 *
 * @param id   Id индустрии
 * @param name Название индустрии
 *
 * @author Daniil Mezev
 */
public record IndustryRequestDTO(
        @NotNull Long id,
        @NotBlank String name
) { }

