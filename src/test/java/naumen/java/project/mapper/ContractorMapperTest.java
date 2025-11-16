package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorResponseDTO;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты ContractorMapper
 *
 * @author Daniil Mezev
 */
class ContractorMapperTest {
    /**
     * Конвертация в ДТО респонс
     */
    @Test
    void toResponse_mapsFields() {
        ContractorMapper mapper = new ContractorMapper();
        Country country = new Country("RU", "Russia");
        Industry industry = new Industry(10L, "IT");
        OrgForm orgForm = new OrgForm("OOO", "ООО");

        Contractor contractor = new Contractor("c-1", "Acme LLC", country, industry, orgForm);

        ContractorResponseDTO dto = mapper.toResponse(contractor);

        Assertions.assertEquals("c-1", dto.id());
        Assertions.assertEquals("Acme LLC", dto.name());
        Assertions.assertEquals("RU", dto.country().id());
        Assertions.assertEquals("Russia", dto.country().name());
        Assertions.assertEquals(10L, dto.industry().id());
        Assertions.assertEquals("IT", dto.industry().name());
        Assertions.assertEquals("OOO", dto.orgForm().id());
        Assertions.assertEquals("ООО", dto.orgForm().name());
    }

}
