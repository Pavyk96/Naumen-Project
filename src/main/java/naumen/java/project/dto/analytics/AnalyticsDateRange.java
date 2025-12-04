package naumen.java.project.dto.analytics;

import jakarta.validation.Valid;

/**
 * Запрос диаппазона дат для аналитики
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
