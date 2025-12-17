package naumen.java.project.dto.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;

import java.util.List;

/**
 * Запрос на экспорт аналитики контрагентов
 *
 * @param format формат экспорта
 * @param dimensions список разрезов (country, industry, org_form, create_year)
 * @param metrics метрики для расчёта
 * @param filters объект фильтров
 * @param includeTrends флаг - включать ли тренды по месяцам
 * @param exportConfig настройки экспорта (имя файла)
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsExportRequest(
        ExportFormat format,
        List<String> dimensions,
        List<String> metrics,
        @Valid ContractorAnalyticsFilters filters,
        boolean includeTrends,
        ExportConfig exportConfig
) { }

