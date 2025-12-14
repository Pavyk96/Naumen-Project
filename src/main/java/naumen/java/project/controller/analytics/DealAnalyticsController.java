package naumen.java.project.controller.analytics;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.deal.request.DealAnalyticsRequestDTO;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.model.Deal;
import naumen.java.project.service.analytics.deal.DealBreakdownService;
import naumen.java.project.service.analytics.deal.DealFilterService;
import naumen.java.project.service.analytics.deal.DealFunnelAnalysisService;
import naumen.java.project.service.analytics.deal.DealPortfolioSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Аналитика по контрагентам и сделкам
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/analytics/deal")
public class DealAnalyticsController {

    private final DealFilterService dealFilterService;
    private final DealPortfolioSummaryService dealPortfolioSummaryService;
    private final DealBreakdownService dealBreakdownService;
    private final DealFunnelAnalysisService dealFunnelAnalysisService;

    public DealAnalyticsController(
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
     * Аналитика по сделкам
     */
    @Transactional
    @PostMapping
    public ResponseEntity<DealAnalyticsResponseDTO> analyzeDeals(
            @Valid @RequestBody DealAnalyticsRequestDTO request
    ) {
        List<Deal> filteredDeals = dealFilterService.applyFiltersDeal(request.filters());
        DealAnalyticsPortfolioSummary portfolioSummary =
                dealPortfolioSummaryService.buildSummaryDeal(filteredDeals);

        List<DealAnalyticsBreakdown> breakdowns =
                request.dimensions() != null && !request.dimensions().isEmpty() ?
                        dealBreakdownService.buildBreakdownsDeal(filteredDeals, request) : null;

        DealAnalyticsFunnelAnalysis funnelAnalysis =
                request.includeFunnel() ?
                        dealFunnelAnalysisService.buildFunnelAnalysisDeal(filteredDeals) : null;

        DealAnalyticsResponseDTO dealAnalyticsResponseDTO = new DealAnalyticsResponseDTO(
                portfolioSummary,
                breakdowns,
                funnelAnalysis
        );
        return ResponseEntity.ok(dealAnalyticsResponseDTO);
    }
}
