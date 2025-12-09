package naumen.java.project.dto.deal;

import naumen.java.project.model.DealStatus;

import java.util.UUID;

/**
 * DTO-ответ для сделки (короткая запись)
 *
 * @param id Идентификатор сделки
 * @param status Статус сделки
 *
 * @author Daria
 */
public record DealShortResponseDTO(
        UUID id,
        DealStatus status
) {}