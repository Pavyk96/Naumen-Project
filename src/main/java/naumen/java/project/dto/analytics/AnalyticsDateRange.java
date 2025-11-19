package naumen.java.project.dto.analytics;

/**
 * Запрос диаппазона дат для аналитики
 *
 * @author Daniil Mezev
 */
public record AnalyticsDateRange(
        LocalDateRange createDate,
        LocalDateRange openedAt,
        LocalDateRange agreementDate
) { }
