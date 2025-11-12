package naumen.java.project.dto.contractor;

/**
 * Информация о контрагенте в сделке
 *
 * @author Daria
 */
public record ContractorInfoForDealDTO(
        /** Идентификатор контрагента */
        String id,
        /** Наименование контрагента */
        String name
) { }