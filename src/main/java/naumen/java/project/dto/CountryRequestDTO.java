package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO-запрос для создания или обновления страны в справочнике
 *
 * @author Daniil Mezev
 */
public record CountryRequestDTO(
        /**
         * Id страны
         */
        @NotBlank String id,
        /**
         * Название страны
         */
        @NotBlank String name
) { }
