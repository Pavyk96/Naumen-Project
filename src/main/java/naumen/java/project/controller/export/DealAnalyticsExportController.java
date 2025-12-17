package naumen.java.project.controller.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsFunnelAnalysis;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.dto.export.DealAnalyticsExportRequestDTO;
import naumen.java.project.model.Deal;
import naumen.java.project.service.analytics.deal.DealBreakdownService;
import naumen.java.project.service.analytics.deal.DealFilterService;
import naumen.java.project.service.analytics.deal.DealFunnelAnalysisService;
import naumen.java.project.service.analytics.deal.DealPortfolioSummaryService;
import naumen.java.project.service.export.deal.DealAnalyticsExporter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер экспорта аналитики по сделкам
 *
 * @author Daria
 */
@RestController
@RequestMapping("/analytics/deal/export")
public class DealAnalyticsExportController {

    private final DealFilterService dealFilterService;
    private final DealPortfolioSummaryService dealPortfolioSummaryService;
    private final DealBreakdownService dealBreakdownService;
    private final DealFunnelAnalysisService dealFunnelAnalysisService;
    private final List<DealAnalyticsExporter> exporters;

    public DealAnalyticsExportController(
            DealFilterService dealFilterService,
            DealPortfolioSummaryService dealPortfolioSummaryService,
            DealBreakdownService dealBreakdownService,
            DealFunnelAnalysisService dealFunnelAnalysisService,
            List<DealAnalyticsExporter> exporters) {
        this.dealFilterService = dealFilterService;
        this.dealPortfolioSummaryService = dealPortfolioSummaryService;
        this.dealBreakdownService = dealBreakdownService;
        this.dealFunnelAnalysisService = dealFunnelAnalysisService;
        this.exporters = exporters;
    }

    /**
     * Экспорт аналитики по сделкам в файл указанного формата
     */
    @Transactional
    @PostMapping
    public ResponseEntity<byte[]> exportDealAnalytics(
            @Valid @RequestBody DealAnalyticsExportRequestDTO request
    ) {
        List<Deal> filteredDeals = dealFilterService.applyFiltersDeal(request.filters());
        DealAnalyticsPortfolioSummary portfolioSummary =
                dealPortfolioSummaryService.buildSummaryDeal(filteredDeals);

        List<DealAnalyticsBreakdown> breakdowns =
                request.dimensions() != null && !request.dimensions().isEmpty() ?
                        dealBreakdownService.buildBreakdownsDeal(filteredDeals, request.dimensions(), request.metrics()) : null;

        DealAnalyticsFunnelAnalysis funnelAnalysis =
                request.includeFunnel() ?
                        dealFunnelAnalysisService.buildFunnelAnalysisDeal(filteredDeals) : null;

        DealAnalyticsResponseDTO dealAnalyticsResponseDTO = new DealAnalyticsResponseDTO(
                portfolioSummary,
                breakdowns,
                funnelAnalysis
        );

        DealAnalyticsExporter exporter = exporters.stream()
                .filter(e -> e.supports() == request.format())
                .findFirst()
                .get();

        byte[] fileBytes = exporter.export(dealAnalyticsResponseDTO);

        String filename = request.exportConfig().filename() + exporter.fileExtension();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileBytes);
    }
}
