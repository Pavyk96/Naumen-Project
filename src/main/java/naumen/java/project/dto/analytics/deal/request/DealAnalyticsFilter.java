package naumen.java.project.dto.analytics.deal.request;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.AnalyticsDateRange;

import java.util.List;

/**
 * DTO для передачи параметров фильтрации аналитики по сделкам
 *
 * @param types Список типов сделок
 * @param statuses Список статусов сделок
 * @param dateRange Диапазон дат
 *
 * @author Daria
 */
public record DealAnalyticsFilter(
        List<String> types,
        List<String> statuses,
        @Valid
        AnalyticsDateRange dateRange
) {}
