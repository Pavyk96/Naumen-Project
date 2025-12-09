package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorResponseDTO;
import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.dto.OrgFormResponseDTO;
import naumen.java.project.model.Contractor;
import org.springframework.stereotype.Component;

/**
 * Маппер для конвертации контрагента из сущности в ДТО
 *
 * @author Daniil Mezev
 */
@Component
public class ContractorMapper {

    /** Конвертация сущности в DTO-ответ */
    public ContractorResponseDTO toResponse(Contractor e) {
        return new ContractorResponseDTO(
                e.getId(),
                e.getName(),
                new CountryResponseDTO(e.getCountry()),
                new IndustryResponseDTO(e.getIndustry()),
                new OrgFormResponseDTO(e.getOrgForm())
        );
    }
}
