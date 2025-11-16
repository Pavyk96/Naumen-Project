package naumen.java.project.mapper;

import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.model.Industry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты IndustryMapper
 *
 * @author Daniil Mezev
 */
class IndustryMapperTest {

    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        IndustryMapper mapper = new IndustryMapper();
        Industry entity = new Industry(10L, "IT");

        IndustryResponseDTO dto = mapper.toResponse(entity);

        Assertions.assertEquals(10L, dto.id());
        Assertions.assertEquals("IT", dto.name());
    }

}
