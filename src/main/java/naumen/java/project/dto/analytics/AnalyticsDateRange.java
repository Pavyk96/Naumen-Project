package naumen.java.project.dto.analytics;

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
        LocalDateRange createDate,
        LocalDateRange openedAt,
        LocalDateRange agreementDate
) { }
