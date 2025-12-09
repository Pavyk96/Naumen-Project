package naumen.java.project.dto;

import naumen.java.project.model.Country;

/**
 * DTO-ответ с полной информацией о стране
 *
 * @param id   Id страны
 * @param name Название страны
 *
 * @author Daniil Mezev
 */
public record CountryResponseDTO(
        String id,
        String name
) {

    /** Создает DTO из сущности Country */
    public CountryResponseDTO(Country entity) {
        this(entity.getId(), entity.getName());
    }
}

