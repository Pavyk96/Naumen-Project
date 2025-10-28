package naumen.java.project.mapper;

import naumen.java.project.dto.OrgFormRequest;
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

    /** Конвертация DTO-запроса в сущность */
    public OrgForm toEntity(OrgFormRequest req) {
        return new OrgForm(req.id().toUpperCase(), req.name());
    }

    /** Конвертация сущности в DTO-ответ */
    public OrgFormResponse toResponse(OrgForm entity) {
        return new OrgFormResponse(entity.getId(), entity.getName());
    }

}
