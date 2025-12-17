package naumen.java.project.dto.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;

import java.util.List;

/**
 * DTO-запрос на экспорт аналитики сделок
 *
 * @param format формат экспорта
 * @param dimensions Список запрашиваемых измерений (группировок)
 * @param metrics Список запрашиваемых метрик
 * @param filters Фильтры, применяемые к выборке данных
 * @param includeFunnel Флаг необходимости включения данных воронки
 * @param exportConfig настройки экспорта (имя файла)
 *
 * @author Daria
 */
public record DealAnalyticsExportRequestDTO (
        ExportFormat format,
        List<String> dimensions,
        List<String> metrics,
        @Valid
        DealAnalyticsFilter filters,
        boolean includeFunnel,
        ExportConfig exportConfig
){}
