package naumen.java.project.service.analytics.deal;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.model.Deal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис фасад построения аналитики по сделкам
 *
 * @author Daria
 */
@Service
public class DealAnalyticsService {

    private final DealFilterService dealFilterService;
    private final DealPortfolioSummaryService dealPortfolioSummaryService;
    private final DealBreakdownService dealBreakdownService;
    private final DealFunnelAnalysisService dealFunnelAnalysisService;

    public DealAnalyticsService(
            DealFilterService dealFilterService,
            DealPortfolioSummaryService dealPortfolioSummaryService,
            DealBreakdownService dealBreakdownService,
            DealFunnelAnalysisService dealFunnelAnalysisService) {
        this.dealFilterService = dealFilterService;
        this.dealPortfolioSummaryService = dealPortfolioSummaryService;
        this.dealBreakdownService = dealBreakdownService;
        this.dealFunnelAnalysisService = dealFunnelAnalysisService;
    }

    /**
     * Собрать аналитику по сделкам
     */
    public DealAnalyticsResponseDTO analyze(DealAnalyticsFilter filters, List<String> dimensions,
                                            List<String> metrics, boolean includeFunnel) {
        List<Deal> filteredDeals = dealFilterService.applyFiltersDeal(filters);
        DealAnalyticsPortfolioSummary portfolioSummary =
                dealPortfolioSummaryService.buildSummaryDeal(filteredDeals);

        List<DealAnalyticsBreakdown> breakdowns =
                dimensions != null && !dimensions.isEmpty()
                        ? dealBreakdownService.buildBreakdownsDeal(filteredDeals, dimensions, metrics)
                        : null;

        DealAnalyticsFunnelAnalysis funnelAnalysis =
                includeFunnel
                        ? dealFunnelAnalysisService.buildFunnelAnalysisDeal(filteredDeals)
                        : null;

        return new DealAnalyticsResponseDTO(
                portfolioSummary,
                breakdowns,
                funnelAnalysis
        );
    }
}
