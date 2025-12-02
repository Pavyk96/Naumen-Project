package naumen.java.project.dto.analytics.contractor.request;

import naumen.java.project.dto.analytics.AnalyticsDateRange;

import java.util.List;

/**
 * Фильтры контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsFilters(
        List<String> countries,
        List<Long> industries,
        List<String> orgForms,
        AnalyticsDateRange dateRange
) { }

