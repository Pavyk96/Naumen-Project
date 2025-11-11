package naumen.java.project.dto.deal;

import naumen.java.project.dto.contractor.ContractorInfoForDeal;

import java.util.List;
import java.util.UUID;

/**
 * DTO-ответ для сделки с детальной информацией
 *
 * @author Daria
 */
public record DealResponse(
        UUID id,
        String description,
        String agreementNumber,
        String agreementDate,
        String openedAt,
        String closedAt,
        String type,
        String status,
        List<ContractorInfoForDeal> contractors
) {
}