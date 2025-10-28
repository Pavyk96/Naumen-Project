package naumen.java.project.mapper;

import naumen.java.project.dto.IndustryRequest;
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

    /** Конвертация DTO-запроса в сущность */
    public Industry toEntity(IndustryRequest req) {
        return new Industry(req.id(), req.name());
    }

    /** Конвертация сущности в DTO-ответ */
    public IndustryResponse toResponse(Industry entity) {
        return new IndustryResponse(entity.getId(), entity.getName());
    }

}
