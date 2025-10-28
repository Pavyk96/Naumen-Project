package naumen.java.project.mapper;

import naumen.java.project.dto.CountryRequest;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.model.Country;
import org.springframework.stereotype.Component;

/**
 * CountryMapper
 *
 * @author Daniil Mezev
 */
@Component
public class CountryMapper {

    /** Конвертация DTO-запроса в сущность */
    public Country toEntity(CountryRequest req) {
        return new Country(req.id().toUpperCase(), req.name());
    }

    /** Конвертация сущности в DTO-ответ */
    public CountryResponse toResponse(Country entity) {
        return new CountryResponse(entity.getId(), entity.getName());
    }

}
