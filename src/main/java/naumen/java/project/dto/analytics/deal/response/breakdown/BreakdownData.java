package naumen.java.project.dto.analytics.deal.response.breakdown;

import com.fasterxml.jackson.annotation.JsonInclude;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsIndustryInfo;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPeriodInfo;

/**
 * DTO для передачи данных в зависимости от типа измерения (dimension)
 *
 * @param period   Информация о временном периоде
 * @param industry Информация об отрасли
 * @param type     Тип сделки
 * @param status   Текущий статус сделки
 * @param metrics  Объект с количественными показателями
 *
 * @author Daria
 * */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BreakdownData(
        DealAnalyticsPeriodInfo period,
        DealAnalyticsIndustryInfo industry,
        String type,
        String status,
        DealAnalyticsMetrics metrics
) {}
