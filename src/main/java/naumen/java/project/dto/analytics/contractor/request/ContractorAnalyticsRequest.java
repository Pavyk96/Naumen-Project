package naumen.java.project.dto.analytics.contractor.request;

import java.util.List;

/**
 * Запрос для аналитики
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsRequest(
        List<String> dimensions,
        List<String> metrics,
        ContractorAnalyticsFilters filters,
        boolean includeTrends
) { }
