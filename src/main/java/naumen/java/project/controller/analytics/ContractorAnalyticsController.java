package naumen.java.project.controller.analytics;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsRequest;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.analytics.contractor.ContractorBreakdownService;
import naumen.java.project.service.analytics.contractor.ContractorFilterService;
import naumen.java.project.service.analytics.contractor.ContractorSummaryService;
import naumen.java.project.service.analytics.contractor.ContractorTrendsService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/analytics/contractor")
public class ContractorAnalyticsController {

    private final ContractorFilterService contractorFilterService;
    private final ContractorSummaryService contractorSummaryService;
    private final ContractorBreakdownService contractorBreakdownService;
    private final ContractorTrendsService contractorTrendsService;

    public ContractorAnalyticsController(
            ContractorFilterService contractorFilterService,
            ContractorSummaryService contractorSummaryService,
            ContractorBreakdownService contractorBreakdownService,
            ContractorTrendsService contractorTrendsService
    ) {
        this.contractorFilterService = contractorFilterService;
        this.contractorSummaryService = contractorSummaryService;
        this.contractorBreakdownService = contractorBreakdownService;
        this.contractorTrendsService = contractorTrendsService;
    }

    /**
     * Аналитика по контрагентам
     */
    @Transactional
    @PostMapping
    public ResponseEntity<ContractorAnalyticsResponse> analyzeContractors(
            @Valid @RequestBody ContractorAnalyticsRequest request
    ) {
        List<Contractor> contractors = contractorFilterService.findContractors(request.filters());

        ContractorAnalyticsSummary summary = contractorSummaryService.buildSummary(contractors);

        List<ContractorAnalyticsBreakdown> breakdowns = contractorBreakdownService.buildBreakdowns(
                contractors,
                request.dimensions(),
                request.metrics()
        );

        ContractorAnalyticsTrends trends = request.includeTrends()
                ? contractorTrendsService.buildTrends(contractors)
                : null;

        ContractorAnalyticsResponse response = new ContractorAnalyticsResponse(
                summary,
                breakdowns,
                trends
        );

        return ResponseEntity.ok(response);
    }
}
