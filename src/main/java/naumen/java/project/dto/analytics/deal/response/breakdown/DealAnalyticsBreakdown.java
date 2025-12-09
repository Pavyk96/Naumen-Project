package naumen.java.project.dto.analytics.deal.response.breakdown;


import java.util.List;

/**
 * Контейнер для одной секции детализации (breakdown) в ответе аналитики
 *
 * @param dimension Название измерения
 * @param data Список конкретных DTO данных, соответствующих измерению
 *
 * @author Daria
 */
public record DealAnalyticsBreakdown(
        String dimension,
        List<BreakdownData> data
) {}
