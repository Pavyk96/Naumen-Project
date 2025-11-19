package naumen.java.project.dto.analytics.contractor.response;

/**
 * Метрики контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsMetrics(
        long count,
        long activeDealsCount
) { }

