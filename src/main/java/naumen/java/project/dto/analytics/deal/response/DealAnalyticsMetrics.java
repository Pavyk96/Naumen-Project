package naumen.java.project.dto.analytics.deal.response;

/**
 * DTO с набором стандартных аналитических метрик для сделок
 *
 * @param count Общее количество сделок, соответствующих критериям
 * @param successRate Процент успешных сделок (коэффициент завершения)
 * @param durationDays Средняя длительность сделки (в днях)
 *
 * @author Daria
 */
public record DealAnalyticsMetrics(
        Long count,
        Double successRate,
        Double durationDays
) {}

