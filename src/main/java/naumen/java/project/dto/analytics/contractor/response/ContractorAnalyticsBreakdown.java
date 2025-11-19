package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Разрез контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsBreakdown(
        String dimension,
        List<ContractorAnalyticsBreakdownData> data
) { }
