package naumen.java.project.dto.deal;

import naumen.java.project.model.DealStatus;

import java.util.UUID;

/**
 * * DTO-ответ для сделки (короткая запись)
 *
 * @author Daria
 */
public record DealShortResponse(
        UUID id,
        DealStatus status
) {}