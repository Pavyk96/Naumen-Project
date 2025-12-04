package naumen.java.project.dto.analytics.deal.response.breakdown;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsIndustryInfo;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPeriodInfo;

/**
 * DTO строки данных аналитики, сгруппированной по времени и отрасли
 *
 * @param period Информация о временном периоде
 * @param industry Информация об отрасли
 * @param metrics Рассчитанные аналитические метрики
 *
 * @author Daria
 */
public record BreakdownTimeIndustry(
        DealAnalyticsPeriodInfo period,
        DealAnalyticsIndustryInfo industry,
        DealAnalyticsMetrics metrics
) implements BreakdownData {}
