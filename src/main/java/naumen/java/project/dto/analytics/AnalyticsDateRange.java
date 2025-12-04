package naumen.java.project.dto.analytics;

import jakarta.validation.Valid;

/**
 * Запрос диаппазона дат для аналитики
 *
 * @param createDate фильтр по дате создания
 * @param openedAt фильтр по дате открытия
 * @param agreementDate фильтр по дате соглашения
 *
 * @author Daniil Mezev
 */
public record AnalyticsDateRange(
        @Valid
        LocalDateRange createDate,
        @Valid
        LocalDateRange openedAt,
        @Valid
        LocalDateRange agreementDate
) { }
