package naumen.java.project.dto.analytics.deal.response;

import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;

import java.util.List;

/**
 * DTO-ответа для аналитического отчета по сделкам
 * Объединяет сводку, детализацию и анализ этапов
 *
 * @param portfolioSummary Сводная общая информация
 * @param breakdown Детализированные данные, сгруппированные по измерениям
 * @param funnelAnalysis Анализ этапов сделки
 *
 * @author Daria
 */
public record DealAnalyticsResponseDTO(
        DealAnalyticsPortfolioSummary portfolioSummary,
        List<DealAnalyticsBreakdown> breakdown,
        DealAnalyticsFunnelAnalysis funnelAnalysis
) {}

