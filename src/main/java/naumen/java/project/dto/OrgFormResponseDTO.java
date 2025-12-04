package naumen.java.project.dto;

import naumen.java.project.model.OrgForm;

/**
 * DTO-ответ с полной информацией об организационно-правовой форме
 *
 * @param id   Id правовой формы
 * @param name Название правовой формы
 *
 * @author Daniil Mezev
 */
public record OrgFormResponseDTO(
        String id,
        String name
) {

    /** Создает DTO из сущности OrgForm */
    public OrgFormResponseDTO(OrgForm entity) {
        this(entity.getId(), entity.getName());
    }
}

