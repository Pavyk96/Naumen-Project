package naumen.java.project.dto;

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
