package naumen.java.project.dto.analytics.deal.response;

/**
 * DTO, содержащий сводную информацию (summary) по сделкам
 *
 * @param totalDeals Общее количество всех сделок
 * @param activeDeals Количество активных сделок
 * @param winRate Процент выигранных (успешных) сделок
 * @param avgRealDuration Средняя фактическая длительность сделок (в днях)
 *
 * @author Daria
 */
public record DealAnalyticsPortfolioSummary(
        Long totalDeals,
        Long activeDeals,
        Double winRate,
        Double avgRealDuration
) {}

