package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Ответ аналитики контрагентов
 *
 * @param summary сводные метрики
 * @param breakdown разрезы аналитики
 * @param trends тренды по периодам
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsResponse(
        ContractorAnalyticsSummary summary,
        List<ContractorAnalyticsBreakdown> breakdown,
        ContractorAnalyticsTrends trends
) { }

