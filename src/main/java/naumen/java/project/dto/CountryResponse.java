package naumen.java.project.dto;

import naumen.java.project.model.Country;

/**
 * DTO-ответ с полной информацией о стране
 *
 * @author Daniil Mezev
 */
public record CountryResponse(String id, String name) {

    /**
     * Создает DTO из сущности Country
     */
    public CountryResponse(Country entity) {
        this(entity.getId(), entity.getName());
    }

}
