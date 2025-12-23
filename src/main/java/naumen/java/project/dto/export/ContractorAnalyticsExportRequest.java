package naumen.java.project.dto.export;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;

import java.util.List;

/**
 * Запрос на экспорт аналитики контрагентов
 *
 * @param dimensions список разрезов (country, industry, org_form, create_year)
 * @param metrics метрики для расчёта
 * @param filters объект фильтров
 * @param includeTrends флаг - включать ли тренды по месяцам
 * @param exportConfig настройки экспорта
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsExportRequest(
        @Valid @NotNull ExportConfig exportConfig,
        List<String> dimensions,
        List<String> metrics,
        @Valid ContractorAnalyticsFilters filters,
        boolean includeTrends
) { }

