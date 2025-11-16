package naumen.java.project.dto;

import naumen.java.project.model.OrgForm;

/**
 * DTO-ответ с полной информацией о организационно-правовой форме
 *
 * @author Daniil Mezev
 */
public record OrgFormResponseDTO(
        /**
         * Id страны
         */
        String id,
        /**
         * Название правовой формы
         */
        String name
) {

    /**
     * Создает DTO из сущности OrgForm
     */
    public OrgFormResponseDTO(OrgForm entity) {
        this(entity.getId(), entity.getName());
    }

}
