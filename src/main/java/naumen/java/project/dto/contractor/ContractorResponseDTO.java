package naumen.java.project.dto.contractor;

import naumen.java.project.dto.CountryResponseDTO;
import naumen.java.project.dto.IndustryResponseDTO;
import naumen.java.project.dto.OrgFormResponseDTO;

/**
 * DTO-ответ с полной информацией о контрагенте
 *
 * @author Daniil Mezev
 */
public record ContractorResponseDTO(
        /**
         * Id контрагента
         */
        String id,
        /**
         * Имя контрагента
         */
        String name,
        /**
         * Страна контрагента
         */
        CountryResponseDTO country,
        /**
         * Индустрия контрагента
         */
        IndustryResponseDTO industry,
        /**
         * Орагнизационна-правовая форма контрагента
         */
        OrgFormResponseDTO orgForm
) { }
