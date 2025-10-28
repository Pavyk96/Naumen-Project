package naumen.java.project.dto;

import naumen.java.project.model.OrgForm;

/**
 * DTO-ответ с полной информацией о организационно-правовой форме
 *
 * @author Daniil Mezev
 */
public record OrgFormResponse(
        String id,
        String name
) {

    /**
     * Создает DTO из сущности OrgForm
     */
    public OrgFormResponse(OrgForm entity) {
        this(entity.getId(), entity.getName());
    }

}
