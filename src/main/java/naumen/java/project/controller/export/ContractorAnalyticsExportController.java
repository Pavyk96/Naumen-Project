package naumen.java.project.controller.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.dto.export.ContractorAnalyticsExportRequest;
import naumen.java.project.dto.export.ExportResult;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.analytics.contractor.ContractorBreakdownService;
import naumen.java.project.service.analytics.contractor.ContractorFilterService;
import naumen.java.project.service.analytics.contractor.ContractorSummaryService;
import naumen.java.project.service.analytics.contractor.ContractorTrendsService;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExportService;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер экспорта аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/analytics/contractor")
public class ContractorAnalyticsExportController {

    private final ContractorFilterService contractorFilterService;
    private final ContractorSummaryService contractorSummaryService;
    private final ContractorBreakdownService contractorBreakdownService;
    private final ContractorTrendsService contractorTrendsService;

    private final ContractorAnalyticsExportService exportService;

    public ContractorAnalyticsExportController(
            ContractorFilterService contractorFilterService,
            ContractorSummaryService contractorSummaryService,
            ContractorBreakdownService contractorBreakdownService,
            ContractorTrendsService contractorTrendsService,
            ContractorAnalyticsExportService exportService
    ) {
        this.contractorFilterService = contractorFilterService;
        this.contractorSummaryService = contractorSummaryService;
        this.contractorBreakdownService = contractorBreakdownService;
        this.contractorTrendsService = contractorTrendsService;
        this.exportService = exportService;
    }

    /**
     * Экспорт аналитики по контрагентам в файл указанного формата
     */
    @Transactional
    @PostMapping("/export")
    public ResponseEntity<byte[]> exportContractorAnalytics(
            @Valid @RequestBody ContractorAnalyticsExportRequest request
    ) {
        String format = request.format();
        if (!exportService.isSupported(format)) {
            throw new IllegalArgumentException(
                    "Формат экспорта '" + format + "' не поддерживается. Доступные форматы: "
                            + exportService.getSupportedFormats()
            );
        }

        ContractorAnalyticsResponse analytics = buildAnalytics(request);

        ExportResult result = exportService.export(
                analytics,
                request.exportConfig().filename(),
                format
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .body(result.fileBytes());
    }

    /**
     * Получить аналитику
     */
    private ContractorAnalyticsResponse buildAnalytics(ContractorAnalyticsExportRequest request) {
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

        return new ContractorAnalyticsResponse(summary, breakdowns, trends);
    }
}
