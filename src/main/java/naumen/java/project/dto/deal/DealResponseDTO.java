package naumen.java.project.dto.deal;

import naumen.java.project.dto.contractor.ContractorInfoForDealDTO;

import java.util.List;
import java.util.UUID;

/**
 * DTO-ответ для сделки с детальной информацией
 *
 * @author Daria
 */
public record DealResponseDTO(
        /** Идентификатор сделки */
        UUID id,
        /** Описание сделки */
        String description,
        /** Номер договора */
        String agreementNumber,
        /** Дата договора */
        String agreementDate,
        /** Дата открытия сделки */
        String openedAt,
        /** Дата закрытия сделки */
        String closedAt,
        /** Тип сделки */
        String type,
        /** Статус сделки */
        String status,
        /** Список контрагентов */
        List<ContractorInfoForDealDTO> contractors
) { }