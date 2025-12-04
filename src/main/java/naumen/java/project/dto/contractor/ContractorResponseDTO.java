package naumen.java.project.dto.contractor;

import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.dto.OrgFormResponseDTO;

import java.util.UUID;

/**
 * DTO-ответ с полной информацией о контрагенте
 *
 * @param id      Id контрагента
 * @param name    Имя контрагента
 * @param country Страна контрагента
 * @param industry Индустрия контрагента
 * @param orgForm Организационно-правовая форма контрагента
 *
 * @author Daniil Mezev
 */
public record ContractorResponseDTO(
        UUID id,
        String name,
        CountryResponseDTO country,
        IndustryResponseDTO industry,
        OrgFormResponseDTO orgForm
) { }

