package naumen.java.project.dto.analytics.deal.response.breakdown;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;

/**
 * DTO строки данных аналитики, сгруппированной по типу и статусу сделки
 *
 * @param type Тип сделки
 * @param status Статус сделки
 * @param metrics Рассчитанные аналитические метрики
 *
 * @author Daria
 */
public record BreakdownTypeStatus(
        String type,
        String status,
        DealAnalyticsMetrics metrics
) implements BreakdownData {}
