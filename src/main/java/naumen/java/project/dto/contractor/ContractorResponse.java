package naumen.java.project.dto.contractor;

import naumen.java.project.dto.CountryResponse;
import naumen.java.project.dto.IndustryResponse;
import naumen.java.project.dto.OrgFormResponse;

/**
 * DTO-ответ с полной информацией о контрагенте
 *
 * @author Daniil Mezev
 */
public record ContractorResponse(
        String id,
        String name,
        CountryResponse country,
        IndustryResponse industry,
        OrgFormResponse orgForm
) { }
