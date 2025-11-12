package naumen.java.project.dto.deal;

import jakarta.validation.constraints.NotBlank;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания или обновления сделки
 *
 * @author Daria
 */
public record DealRequestDTO(
        /** Уникальный идентификатор сделки */
        @ValidUuid String id,
        /** Описание сделки */
        @NotBlank String description,
        /** Номер договора */
        @NotBlank String agreementNumber,
        /** Дата договора */
        @NotBlank String agreementDate,
        /** Дата открытия сделки */
        String openedAt,
        /** Дата закрытия сделки */
        String closedAt,
        /** Тип сделки */
        @NotBlank String type,
        /** Статус сделки */
        String status
) { }
