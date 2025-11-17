package naumen.java.project.mapper;

import naumen.java.project.dto.OrgFormResponseDTO;
import naumen.java.project.model.OrgForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты OrgFormMapper
 *
 * @author Daniil Mezev
 */
class OrgFormMapperTest {

    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        OrgFormMapper mapper = new OrgFormMapper();
        OrgForm entity = new OrgForm("OOO", "ООО");

        OrgFormResponseDTO dto = mapper.toResponse(entity);

        Assertions.assertEquals("OOO", dto.id());
        Assertions.assertEquals("ООО", dto.name());
    }

}
