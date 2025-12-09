package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания связи сделка-контрагент
 *
 * @param dealId Идентификатор сделки
 * @param contractorId Идентификатор контрагента
 *
 * @author Daria
 */
public record DealContractorRequestDTO(
        @NotBlank @ValidUuid String dealId,
        @NotBlank @ValidUuid String contractorId
) { }