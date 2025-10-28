package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * IndustryRequest
 *
 * @author Daniil Mezev
 */
public record IndustryRequest(
        @NotNull Long id,
        @NotBlank String name
) { }
