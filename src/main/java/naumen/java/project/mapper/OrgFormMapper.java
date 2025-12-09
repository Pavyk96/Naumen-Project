package naumen.java.project.mapper;

import naumen.java.project.dto.OrgFormResponseDTO;
import naumen.java.project.model.OrgForm;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации правовой формы из сущности в ДТО
 *
 * @author Daniil Mezev
 */
@Component
public class OrgFormMapper {

    /** Конвертация сущности в DTO-ответ */
    public OrgFormResponseDTO toResponse(OrgForm entity) {
        return new OrgFormResponseDTO(entity.getId(), entity.getName());
    }

}
