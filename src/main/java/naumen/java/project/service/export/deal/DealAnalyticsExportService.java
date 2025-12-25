package naumen.java.project.service.export.deal;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.export.ExportConfig;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;
import naumen.java.project.service.analytics.deal.DealAnalyticsService;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис экспорта аналитики сделок
 * Выбирает нужного экспортера по формату и формирует файл
 *
 * @author Daria
 */
@Service
public class DealAnalyticsExportService {

    private final DealAnalyticsService dealAnalyticsService;
    private final Map<ExportFormat, DealAnalyticsExporter> exporters;

    public DealAnalyticsExportService(
            DealAnalyticsService dealAnalyticsService,
            List<DealAnalyticsExporter> exporters
    ) {
        this.dealAnalyticsService = dealAnalyticsService;
        this.exporters = exporters.stream()
                .collect(Collectors.toMap(
                        DealAnalyticsExporter::getSupport,
                        Function.identity(),
                        (format, exporter) -> {
                            throw new IllegalStateException(
                                    "Дубликат экспортера для формата: " + exporter.getSupport()
                            );
                        }
                ));

        validateAllFormatsSupported();
    }

    /**
     * Проверить, что для всех значений ExportFormat существует экспортер.
     */
    private void validateAllFormatsSupported() {
        EnumSet<ExportFormat> missingFormats = EnumSet.allOf(ExportFormat.class);
        missingFormats.removeAll(exporters.keySet());

        if (!missingFormats.isEmpty()) {
            throw new IllegalStateException(
                    "Не реализованы экспортеры для форматов: " + missingFormats
            );
        }
    }

    /**
     * Экспортировать аналитику сделок в файл указанном формата
     */
    public ExportFile export(
            ExportConfig exportConfig,
            DealAnalyticsFilter filters,
            List<String> dimensions,
            List<String> metrics,
            boolean includeFunnel
    ) {
        DealAnalyticsResponseDTO analytics = dealAnalyticsService.analyze(
                filters, dimensions, metrics, includeFunnel
        );

        DealAnalyticsExporter exporter = exporters.get(exportConfig.exportFormat());

        return exporter.export(
                exportConfig.filename() + exportConfig.exportFormat().getDisplayName(),
                analytics
        );
    }
}
