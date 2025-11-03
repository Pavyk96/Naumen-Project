package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO-запрос для создания связи сделка-контрагент
 *
 * @author Daria
 */
public record DealContractorRequest(
        @NotBlank String dealId,
        @NotBlank String contractorId
) {
}