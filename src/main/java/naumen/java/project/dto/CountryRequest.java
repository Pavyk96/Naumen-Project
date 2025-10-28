package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO-запрос для создания или обновления страны в справочнике
 *
 * @author Daniil Mezev
 */
public record CountryRequest(
        @NotBlank @Size(min = 2, max = 3) String id,
        @NotBlank String name
) { }
