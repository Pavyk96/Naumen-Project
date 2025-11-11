package naumen.java.project.mapper;

import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.model.Industry;
import org.springframework.stereotype.Component;

/**
 * IndustryMapper
 *
 * @author Daniil Mezev
 */
@Component
public class IndustryMapper {

    /** Конвертация сущности в DTO-ответ */
    public IndustryResponse toResponse(Industry entity) {
        return new IndustryResponse(entity.getId(), entity.getName());
    }

}
