package naumen.java.project.mapper;

import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.model.OrgForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrgFormMapperTest {

    @Test
    void toResponse_mapsFields() {
        OrgFormMapper mapper = new OrgFormMapper();
        OrgForm entity = new OrgForm("OOO", "ООО");

        OrgFormResponse dto = mapper.toResponse(entity);

        assertEquals("OOO", dto.id());
        assertEquals("ООО", dto.name());
    }

}
