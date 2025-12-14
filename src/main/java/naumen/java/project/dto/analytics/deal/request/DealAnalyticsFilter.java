package naumen.java.project.dto.analytics.deal.request;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.AnalyticsDateRange;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.validation.ValidEnum;

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
        List<@ValidEnum(enumClass = DealType.class) String> types,
        List<@ValidEnum(enumClass = DealStatus.class) String> statuses,
        @Valid
        AnalyticsDateRange dateRange
) {}
