package naumen.java.project.dto.analytics.deal.response;

import java.util.List;

/**
 * DTO для представления результатов анализа сделок
 *
 * @param stages Список этапов сделок и их метрик
 * @param avgSalesCycle Средняя длительность цикла сделки (в днях)
 *
 * @author Daria
 */
public record DealAnalyticsFunnelAnalysis(
        List<DealAnalyticsFunnelStage> stages,
        Double avgSalesCycle
) {}
