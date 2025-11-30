package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO-запрос для создания или обновления страны в справочнике
 *
 * @param id   Id страны
 * @param name Название страны
 *
 * @author Daniil Mezev
 */
public record CountryRequestDTO(
        @NotBlank String id,
        @NotBlank String name
) { }

