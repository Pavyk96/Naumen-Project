package naumen.java.project.dto.analytics.contractor.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Метрики контрагентов
 *
 * @param count общее количество сделок
 * @param activeDealsCount количество активных сделок
 *
 * @author Daniil Mezev
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContractorAnalyticsMetrics(
        Long count,
        Long activeDealsCount
) { }

