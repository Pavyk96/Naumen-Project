package naumen.java.project.dto.deal;

import jakarta.validation.constraints.NotBlank;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания или обновления сделки
 *
 * @author Daria
 */
public record DealRequest(
        @ValidUuid String id,
        @NotBlank String description,
        @NotBlank String agreementNumber,
        @NotBlank String agreementDate,
        String openedAt,
        String closedAt,
        @NotBlank String type,
        String status
) { }
