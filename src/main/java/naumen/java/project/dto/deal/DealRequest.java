package naumen.java.project.dto.deal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO-запрос для создания или обновления сделки
 *
 * @author Daria
 */
public record DealRequest(
        String id,
        @NotBlank String description,
        @NotBlank String agreementNumber,
        @NotBlank String agreementDate,
        String openedAt,
        String closedAt,
        @NotBlank String type,
        String status
) { }
