package naumen.java.project.mapper;

import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.model.Industry;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации индустрии из сущности в ДТО
 *
 * @author Daniil Mezev
 */
@Component
public class IndustryMapper {

    /** Конвертация сущности в DTO-ответ */
    public IndustryResponseDTO toResponse(Industry entity) {
        return new IndustryResponseDTO(entity.getId(), entity.getName());
    }

}
