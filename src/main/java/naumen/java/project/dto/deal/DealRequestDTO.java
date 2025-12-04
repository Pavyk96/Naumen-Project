package naumen.java.project.dto.deal;

import jakarta.validation.constraints.NotBlank;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.validation.ValidEnum;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания или обновления сделки
 *
 * @param id Уникальный идентификатор сделки
 * @param description Описание сделки
 * @param agreementNumber Номер договора
 * @param agreementDate Дата договора
 * @param openedAt Дата открытия сделки
 * @param closedAt Дата закрытия сделки
 * @param type Тип сделки
 * @param status Статус сделки
 *
 * @author Daria
 */
public record DealRequestDTO(
        @ValidUuid String id,
        @NotBlank String description,
        @NotBlank String agreementNumber,
        @NotBlank String agreementDate,
        String openedAt,
        String closedAt,
        @NotBlank
        @ValidEnum(enumClass = DealType.class)
        String type,
        @ValidEnum(enumClass = DealStatus.class)
        String status
) { }
