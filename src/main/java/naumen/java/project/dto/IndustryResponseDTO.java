package naumen.java.project.dto;

import naumen.java.project.model.Industry;

/**
 * DTO-ответ с полной информацией о индустрии
 *
 * @author Daniil Mezev
 */
public record IndustryResponseDTO(
        /**
         * Id страны
         */
        Long id,
        /**
         * Название индустрии
         */
        String name
) {

    /**
     * Создает DTO из сущности Industry
     */
    public IndustryResponseDTO(Industry entity) {
        this(entity.getId(), entity.getName());
    }

}
