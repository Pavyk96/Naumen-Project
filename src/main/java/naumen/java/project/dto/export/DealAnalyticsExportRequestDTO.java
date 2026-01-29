package naumen.java.project.dto.export;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;

import java.util.List;

/**
 * DTO-запрос на экспорт аналитики сделок
 *
 * @param exportConfig настройки экспорта
 * @param dimensions Список запрашиваемых измерений (группировок)
 * @param metrics Список запрашиваемых метрик
 * @param filters Фильтры, применяемые к выборке данных
 * @param includeFunnel Флаг необходимости включения данных воронки
 *
 * @author Daria
 */
public record DealAnalyticsExportRequestDTO (
        @Valid @NotNull ExportConfig exportConfig,
        List<String> dimensions,
        List<String> metrics,
        @Valid
        DealAnalyticsFilter filters,
        boolean includeFunnel
){}
