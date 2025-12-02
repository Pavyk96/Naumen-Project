package naumen.java.project.dto;

import naumen.java.project.model.Industry;

/**
 * DTO-ответ с полной информацией об индустрии
 *
 * @param id   Id индустрии
 * @param name Название индустрии
 *
 * @author Daniil Mezev
 */
public record IndustryResponseDTO(
        Long id,
        String name
) {

    /** Создает DTO из сущности Industry */
    public IndustryResponseDTO(Industry entity) {
        this(entity.getId(), entity.getName());
    }
}

