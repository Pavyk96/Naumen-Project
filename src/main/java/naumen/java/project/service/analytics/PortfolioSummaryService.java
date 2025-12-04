package naumen.java.project.service.analytics;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.utils.Calculator;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Daria
 */
@Service
public class PortfolioSummaryService {
    private final Calculator calculator;

    public PortfolioSummaryService(Calculator calculator) {
        this.calculator = calculator;
    }

    public DealAnalyticsPortfolioSummary buildSummaryDeal(List<Deal> deals) {
        long totalDeals = deals.size();
        long activeDeals = countActiveDeals(deals);
        double winRate = calculator.calculateWinRate(deals);
        double avgRealDuration = calculator.calculateDurationDays(deals);

        return new DealAnalyticsPortfolioSummary(totalDeals, activeDeals, winRate, avgRealDuration);
    }

    private long countActiveDeals(List<Deal> deals) {
        return deals.stream()
                .filter(d -> d.getStatus() == DealStatus.ACTIVE)
                .count();
    }
}
