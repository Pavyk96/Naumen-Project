package naumen.java.project.dto.analytics.contractor.request;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.AnalyticsDateRange;

import java.util.List;

/**
 * Фильтры контрагентов
 *
 * @param countries  список ID стран
 * @param industries список ID индустрий
 * @param orgForms   список ID организационно-правовых форм
 * @param dateRange  диапазоны дат для фильтрации
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsFilters(
        List<String> countries,
        List<Long> industries,
        List<String> orgForms,
        @Valid
        AnalyticsDateRange dateRange
) { }

