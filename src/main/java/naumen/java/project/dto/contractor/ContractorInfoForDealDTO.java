package naumen.java.project.dto.contractor;

import java.util.UUID;

/**
 * Информация о контрагенте в сделке
 *
 * @param id   Идентификатор контрагента
 * @param name Наименование контрагента
 *
 * @author Daria
 */
public record ContractorInfoForDealDTO(
        UUID id,
        String name
) { }
