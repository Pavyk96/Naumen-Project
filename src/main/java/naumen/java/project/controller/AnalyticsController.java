package naumen.java.project.controller;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsRequest;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.deal.request.DealAnalyticsRequestDTO;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.model.Deal;
import naumen.java.project.service.ContractorAnalyticsService;
import naumen.java.project.service.analytics.BreakdownService;
import naumen.java.project.service.analytics.FilterService;
import naumen.java.project.service.analytics.FunnelAnalysisService;
import naumen.java.project.service.analytics.PortfolioSummaryService;
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
@RequestMapping("/analytics")
public class AnalyticsController {

    private final ContractorAnalyticsService contractorAnalyticsService;
    private final FilterService filterService;
    private final PortfolioSummaryService portfolioSummaryService;
    private final BreakdownService breakdownService;
    private final FunnelAnalysisService funnelAnalysisService;


    public AnalyticsController(
            ContractorAnalyticsService contractorAnalyticsService,
            FilterService filterService,
            PortfolioSummaryService portfolioSummaryService,
            BreakdownService breakdownService,
            FunnelAnalysisService funnelAnalysisService) {
        this.contractorAnalyticsService = contractorAnalyticsService;
        this.filterService = filterService;
        this.portfolioSummaryService = portfolioSummaryService;
        this.breakdownService = breakdownService;
        this.funnelAnalysisService = funnelAnalysisService;
    }

    /**
     * Аналитика по контрагентам
     */
    @Transactional
    @PostMapping("/contractor")
    public ResponseEntity<ContractorAnalyticsResponse> analyzeContractors(
            @Valid @RequestBody ContractorAnalyticsRequest request
    ) {
        return ResponseEntity.ok(contractorAnalyticsService.analyze(request));
    }

    /**
     * Аналитика по сделкам
     */
    @Transactional
    @PostMapping("/deal")
    public ResponseEntity<DealAnalyticsResponseDTO> analyzeDeals(
            @Valid @RequestBody DealAnalyticsRequestDTO request
    ) {
        List<Deal> filteredDeals = filterService.applyFiltersDeal(request.filters());
        DealAnalyticsPortfolioSummary portfolioSummary =
                portfolioSummaryService.buildSummaryDeal(filteredDeals);

        List<DealAnalyticsBreakdown> breakdowns =
                request.dimensions() != null && !request.dimensions().isEmpty() ?
                        breakdownService.buildBreakdownsDeal(filteredDeals, request) : null;

        DealAnalyticsFunnelAnalysis funnelAnalysis =
                request.includeFunnel() ?
                        funnelAnalysisService.buildFunnelAnalysisDeal(filteredDeals) : null;

        DealAnalyticsResponseDTO dealAnalyticsResponseDTO = new DealAnalyticsResponseDTO(
                portfolioSummary,
                breakdowns,
                funnelAnalysis
        );
        return ResponseEntity.ok(dealAnalyticsResponseDTO);
    }
}
