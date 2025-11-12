package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания связи сделка-контрагент
 *
 * @author Daria
 */
public record DealContractorRequestDTO(
        /** Идентификатор сделки */
        @NotBlank @ValidUuid String dealId,
        /** Идентификатор контрагента */
        @NotBlank String contractorId
) { }