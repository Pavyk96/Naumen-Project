package naumen.java.project.dto;

import naumen.java.project.model.Country;

/**
 * DTO-ответ с полной информацией о стране
 *
 * @author Daniil Mezev
 */
public record CountryResponseDTO(
        /**
         * Id страны
         */
        String id,
        /**
         * Название страны
         */
        String name
) {

    /**
     * Создает DTO из сущности Country
     */
    public CountryResponseDTO(Country entity) {
        this(entity.getId(), entity.getName());
    }

}
