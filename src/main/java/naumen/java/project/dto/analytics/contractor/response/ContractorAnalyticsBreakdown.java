package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Разрез контрагентов по параметрам
 *
 * @param dimension тип разреза
 * @param data данные разреза с метриками
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsBreakdown(
        String dimension,
        List<ContractorAnalyticsBreakdownData> data
) { }
