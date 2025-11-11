package naumen.java.project.mapper;

import naumen.java.project.dto.CountryResponse;
import naumen.java.project.model.Country;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryMapperTest {

    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        CountryMapper mapper = new CountryMapper();
        Country entity = new Country("RU", "Russia");

        CountryResponse dto = mapper.toResponse(entity);

        assertEquals("RU", dto.id());
        assertEquals("Russia", dto.name());
    }

}
