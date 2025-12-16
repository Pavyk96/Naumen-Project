package naumen.java.project.controller.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.dto.export.ContractorAnalyticsExportRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.analytics.contractor.ContractorBreakdownService;
import naumen.java.project.service.analytics.contractor.ContractorFilterService;
import naumen.java.project.service.analytics.contractor.ContractorSummaryService;
import naumen.java.project.service.analytics.contractor.ContractorTrendsService;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер экспорта аналитики по контрагентам
 *
 * @author Daniil
 */
@RestController
@RequestMapping("/analytics/contractor")
public class ContractorAnalyticsExportController {

    private final ContractorFilterService contractorFilterService;
    private final ContractorSummaryService contractorSummaryService;
    private final ContractorBreakdownService contractorBreakdownService;
    private final ContractorTrendsService contractorTrendsService;

    private final List<ContractorAnalyticsExporter> exporters;

    public ContractorAnalyticsExportController(
            ContractorFilterService contractorFilterService,
            ContractorSummaryService contractorSummaryService,
            ContractorBreakdownService contractorBreakdownService,
            ContractorTrendsService contractorTrendsService,
            List<ContractorAnalyticsExporter> exporters
    ) {
        this.contractorFilterService = contractorFilterService;
        this.contractorSummaryService = contractorSummaryService;
        this.contractorBreakdownService = contractorBreakdownService;
        this.contractorTrendsService = contractorTrendsService;
        this.exporters = exporters;
    }

    /**
     * Экспорт аналитики по контрагентам в файл указанного формата
     */
    @Transactional
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportContractorAnalytics(
            @Valid @RequestBody ContractorAnalyticsExportRequest request
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

        ContractorAnalyticsResponse analytics = new ContractorAnalyticsResponse(summary, breakdowns, trends);

        ContractorAnalyticsExporter exporter = exporters.stream()
                .filter(e -> e.supports() == request.format())
                .findFirst()
                .get();

        byte[] fileBytes = exporter.export(analytics);

        String filename = request.exportConfig().filename() + exporter.fileExtension();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileBytes);
    }
}
