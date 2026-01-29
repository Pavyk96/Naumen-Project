package naumen.java.project.controller.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.export.DealAnalyticsExportRequestDTO;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.service.export.deal.DealAnalyticsExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер экспорта аналитики по сделкам
 *
 * @author Daria
 */
@RestController
@RequestMapping("/analytics/deal/export")
public class DealAnalyticsExportController {

    private final DealAnalyticsExportService dealAnalyticsExportService;

    public DealAnalyticsExportController(DealAnalyticsExportService dealAnalyticsExportService) {
        this.dealAnalyticsExportService = dealAnalyticsExportService;
    }

    /**
     * Экспорт аналитики по сделкам в файл указанного формата
     */
    @Transactional
    @PostMapping
    public ResponseEntity<byte[]> exportDealAnalytics(
            @Valid @RequestBody DealAnalyticsExportRequestDTO request
    ) {
        ExportFile file = dealAnalyticsExportService.export(
                request.exportConfig(),
                request.filters(),
                request.dimensions(),
                request.metrics(),
                request.includeFunnel()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
                .body(file.content());
    }
}
