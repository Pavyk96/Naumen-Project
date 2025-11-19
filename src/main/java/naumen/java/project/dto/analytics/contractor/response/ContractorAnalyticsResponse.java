package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Ответ аналитики контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsResponse(
        ContractorAnalyticsSummary summary,
        List<ContractorAnalyticsBreakdown> breakdown,
        ContractorAnalyticsTrends trends
) { }

