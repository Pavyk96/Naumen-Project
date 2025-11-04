package naumen.java.project.mapper;

import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.model.Industry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndustryMapperTest {

    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        IndustryMapper mapper = new IndustryMapper();
        Industry entity = new Industry(10L, "IT");

        IndustryResponse dto = mapper.toResponse(entity);

        assertEquals(10L, dto.id());
        assertEquals("IT", dto.name());
    }

}
