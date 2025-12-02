package naumen.java.project.dto.analytics.contractor.response;

import java.util.Map;

/**
 * Данные разреза
 *
 * @param group информация о группе
 * @param metrics метрики группы
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsBreakdownData(
        Map<String, Object> group,
        ContractorAnalyticsMetrics metrics
) { }

