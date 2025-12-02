package naumen.java.project.dto.analytics.contractor.response;

/**
 * Метрики контрагентов
 *
 * @param count общее количество сделок
 * @param activeDealsCount количество активных сделок
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsMetrics(
        long count,
        long activeDealsCount
) { }

