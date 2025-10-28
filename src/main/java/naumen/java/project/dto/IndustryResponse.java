package naumen.java.project.dto;

import naumen.java.project.model.Industry;

/**
 * DTO-ответ с полной информацией о индустрии
 *
 * @author Daniil Mezev
 */
public record IndustryResponse(
        Long id,
        String name
) {

    /**
     * Создает DTO из сущности Industry
     */
    public IndustryResponse(Industry entity) {
        this(entity.getId(), entity.getName());
    }

}
