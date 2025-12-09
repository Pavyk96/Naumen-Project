package naumen.java.project.dto.analytics.deal.request;

import jakarta.validation.Valid;

import java.util.List;

/**
 * DTO-запрос для аналитических данных по сделкам
 *
 * @param dimensions Список запрашиваемых измерений (группировок)
 * @param metrics Список запрашиваемых метрик
 * @param filters Фильтры, применяемые к выборке данных
 * @param includeFunnel Флаг необходимости включения данных воронки
 *
 * @author Daria
 */
public record DealAnalyticsRequestDTO(
        List<String> dimensions,
        List<String> metrics,
        @Valid
        DealAnalyticsFilter filters,
        boolean includeFunnel
) {}
