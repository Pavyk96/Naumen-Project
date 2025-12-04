package naumen.java.project.service.analytics;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelStage;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.utils.Calculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daria
 */
@Service
public class FunnelAnalysisService {
    private final Calculator calculator;

    public FunnelAnalysisService(Calculator calculator) {
        this.calculator = calculator;
    }

    public DealAnalyticsFunnelAnalysis buildFunnelAnalysisDeal(List<Deal> deals) {
        List<DealAnalyticsFunnelStage> stages = buildFunnelStages(deals);
        double avgSalesCycle = calculator.calculateAvgSalesCycle(deals);
        return new DealAnalyticsFunnelAnalysis(stages, avgSalesCycle);
    }

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
