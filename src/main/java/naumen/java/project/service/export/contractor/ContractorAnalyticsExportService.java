package naumen.java.project.service.export.contractor;

import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.export.ExportConfig;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;
import naumen.java.project.service.analytics.contractor.ContractorAnalyticsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис экспорта аналитики контрагентов.
 * Выбирает нужного экспортера по формату и формирует файл.
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorAnalyticsExportService {

    private final ContractorAnalyticsService contractorAnalyticsService;
    private final Map<ExportFormat, ContractorAnalyticsExporter> exporters;

    /**
     * Создать сервис и зарегистрировать доступные экспортеры.
     *
     * @param contractorAnalyticsService сервис построения аналитики
     * @param exporters список реализаций экспортеров
     */
    public ContractorAnalyticsExportService(
            ContractorAnalyticsService contractorAnalyticsService,
            List<ContractorAnalyticsExporter> exporters
    ) {
        this.contractorAnalyticsService = contractorAnalyticsService;
        this.exporters = exporters.stream()
                .collect(Collectors.toMap(
                        ContractorAnalyticsExporter::getSupport,
                        Function.identity(),
                        (format, exporter) -> {
                            throw new IllegalStateException(
                                    "Дубликат экспортера для формата: " + exporter.getSupport()
                            );
                        }
                ));
    }

    /**
     * Экспортировать аналитику контрагентов в файл заданного формата
     *
     * @param exportConfig настройки экспорта (формат, имя файла)
     * @param filters фильтры аналитики
     * @param dimensions разрезы аналитики
     * @param metrics метрики аналитики
     * @param includeTrends включать ли тренды
     *
     * @return сформированный файл
     */
    public ExportFile export(
            ExportConfig exportConfig,
            ContractorAnalyticsFilters filters,
            List<String> dimensions,
            List<String> metrics,
            boolean includeTrends
    ) {
        ExportFormat format = exportConfig.exportFormat();

        ContractorAnalyticsExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new IllegalArgumentException(
                    "Формат экспорта " + format + " не поддерживается. Доступные форматы: " + exporters.keySet()
            );
        }

        ContractorAnalyticsResponse analytics = contractorAnalyticsService.analyze(
                filters, dimensions, metrics, includeTrends
        );

        return exporter.export(exportConfig.filename(), analytics);
    }
}
