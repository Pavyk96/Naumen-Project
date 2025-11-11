package naumen.java.project.mapper;

import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.model.OrgForm;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между сущностью OrgForm и её DTO.
 *
 * @author Daniil Mezev
 */
@Component
public class OrgFormMapper {

    /** Конвертация сущности в DTO-ответ */
    public OrgFormResponse toResponse(OrgForm entity) {
        return new OrgFormResponse(entity.getId(), entity.getName());
    }

}
