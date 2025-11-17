package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * IndustryRequestDTO
 *
 * @author Daniil Mezev
 */
public record IndustryRequestDTO(
        /**
         * Id индустрии
         */
        @NotNull Long id,
        /**
         * Название индустрии
         */
        @NotBlank String name
) { }
