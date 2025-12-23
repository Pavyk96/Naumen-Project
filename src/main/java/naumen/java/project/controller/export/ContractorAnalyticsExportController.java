package naumen.java.project.controller.export;

import jakarta.validation.Valid;
import naumen.java.project.dto.export.ContractorAnalyticsExportRequest;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер экспорта аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/analytics/contractor")
public class ContractorAnalyticsExportController {

    private final ContractorAnalyticsExportService exportService;

    public ContractorAnalyticsExportController(ContractorAnalyticsExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * Экспортировать аналитику по контрагентам в файл указанного формата
     */
    @Transactional
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@Valid @RequestBody ContractorAnalyticsExportRequest request) {
        ExportFile file = exportService.export(
                request.exportConfig(),
                request.filters(),
                request.dimensions(),
                request.metrics(),
                request.includeTrends()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.fileName() + "\"")
                .body(file.content());
    }
}
