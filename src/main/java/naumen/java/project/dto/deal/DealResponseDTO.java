package naumen.java.project.dto.deal;

import naumen.java.project.dto.contractor.ContractorInfoForDealDTO;

import java.util.List;
import java.util.UUID;

/**
 * DTO-ответ для сделки с детальной информацией
 *
 * @param id Идентификатор сделки
 * @param description Описание сделки
 * @param agreementNumber Номер договора
 * @param agreementDate Дата договора
 * @param openedAt Дата открытия сделки
 * @param closedAt Дата закрытия сделки
 * @param type Тип сделки
 * @param status Статус сделки
 * @param contractors Список контрагентов
 *
 * @author Daria
 */
public record DealResponseDTO(
        UUID id,
        String description,
        String agreementNumber,
        String agreementDate,
        String openedAt,
        String closedAt,
        String type,
        String status,
        List<ContractorInfoForDealDTO> contractors
) { }