package naumen.java.project.mapper;

import naumen.java.project.dto.ContractorRequest;
import naumen.java.project.dto.ContractorResponse;
import naumen.java.project.dto.CountryResponse;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.dto.OrgFormResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import org.springframework.stereotype.Component;

/**
 * ContractorMapper
 *
 * @author Daniil Mezev
 */
@Component
public class ContractorMapper {

    /** Конвертация DTO-запроса в сущность */
    public Contractor toEntity(ContractorRequest req) {
        return new Contractor(
                req.id(),
                req.name(),
                req.countryId(),
                req.industryId(),
                req.orgFormId()
        );
    }

    /** Конвертация сущности в DTO-ответ */
    public ContractorResponse toResponse(Contractor entity, Country country, Industry industry, OrgForm orgForm) {
        return new ContractorResponse(
                entity.getId(),
                entity.getName(),
                new CountryResponse(country),
                new IndustryResponse(industry),
                new OrgFormResponse(orgForm)
        );
    }

}
