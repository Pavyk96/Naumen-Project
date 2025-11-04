package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractorMapperTest {
    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        ContractorMapper mapper = new ContractorMapper();

        Contractor contractor = new Contractor("c-1", "Acme LLC", "RU", 10L, "OOO");
        Country country = new Country("RU", "Russia");
        Industry industry = new Industry(10L, "IT");
        OrgForm orgForm = new OrgForm("OOO", "ООО");

        ContractorResponse dto = mapper.toResponse(contractor, country, industry, orgForm);

        assertEquals("c-1", dto.id());
        assertEquals("Acme LLC", dto.name());
        assertEquals("RU", dto.country().id());
        assertEquals("Russia", dto.country().name());
        assertEquals(10L, dto.industry().id());
        assertEquals("IT", dto.industry().name());
        assertEquals("OOO", dto.orgForm().id());
        assertEquals("ООО", dto.orgForm().name());
    }

}
