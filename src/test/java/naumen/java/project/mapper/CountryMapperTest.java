package naumen.java.project.mapper;

import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.model.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты CountryMapper
 *
 * @author Daniil Mezev
 */
class CountryMapperTest {

    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        CountryMapper mapper = new CountryMapper();
        Country entity = new Country("RU", "Russia");

        CountryResponseDTO dto = mapper.toResponse(entity);

        Assertions.assertEquals("RU", dto.id());
        Assertions.assertEquals("Russia", dto.name());
    }

}
