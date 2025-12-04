package naumen.java.project.dto.analytics.contractor.request;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Запрос для аналитики
 *
 * @param dimensions    список разрезов (country, industry, org_form, create_year)
 * @param metrics       метрики для расчёта
 * @param filters       объект фильтров
 * @param includeTrends флаг — включать ли тренды по месяцам
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsRequest(
        List<String> dimensions,
        List<String> metrics,
        @Valid
        ContractorAnalyticsFilters filters,
        boolean includeTrends
) { }
