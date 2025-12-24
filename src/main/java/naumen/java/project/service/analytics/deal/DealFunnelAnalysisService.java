package naumen.java.project.service.analytics.deal;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelStage;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.utils.Calculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, отвечающий за построение воронки типов и расчет ключевых метрик
 * на основе списка сделок
 *
 * @author Daria
 */
@Service
public class DealFunnelAnalysisService {
    private final Calculator calculator;

    public DealFunnelAnalysisService(Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Строит полный объект анализа воронки, включая все стадии и средний цикл
     */
    public DealAnalyticsFunnelAnalysis buildFunnelAnalysisDeal(List<Deal> deals) {
        List<DealAnalyticsFunnelStage> stages = buildFunnelStages(deals);
        double avgSalesCycle = calculator.calculateAvgSalesCycle(deals);
        return new DealAnalyticsFunnelAnalysis(stages, avgSalesCycle);
    }

    /**
     * Генерирует список стадий воронки на основе предоставленного списка сделок
     */
    private List<DealAnalyticsFunnelStage> buildFunnelStages(List<Deal> deals) {
        List<DealAnalyticsFunnelStage> stages = new ArrayList<>();
        long totalDeals = deals.size();

        for (DealStatus status : DealStatus.values()) {
            List<Deal> statusDeals = deals.stream()
                    .filter(deal -> deal.getStatus() == status)
                    .toList();
            long statusDealsCount = statusDeals.size();

            stages.add(new DealAnalyticsFunnelStage(
                    status.getDisplayName(),
                    statusDealsCount,
                    totalDeals > 0 ? (double) statusDealsCount / totalDeals : 0.0,
                    calculator.calculateDurationDays(statusDeals)
            ));
        }

        return stages;
    }
}