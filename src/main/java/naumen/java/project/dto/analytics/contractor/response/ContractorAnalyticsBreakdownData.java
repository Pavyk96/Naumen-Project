package naumen.java.project.dto.analytics.contractor.response;

import java.util.Map;

/**
 * Данные разреза
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsBreakdownData(
        Map<String, Object> group,
        ContractorAnalyticsMetrics metrics
) { }

