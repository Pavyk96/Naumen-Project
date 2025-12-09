package naumen.java.project.mapper;

import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.model.Country;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации страны из сущности в ДТО
 *
 * @author Daniil Mezev
 */
@Component
public class CountryMapper {

    /** Конвертация сущности в DTO-ответ */
    public CountryResponseDTO toResponse(Country entity) {
        return new CountryResponseDTO(entity.getId(), entity.getName());
    }

}
