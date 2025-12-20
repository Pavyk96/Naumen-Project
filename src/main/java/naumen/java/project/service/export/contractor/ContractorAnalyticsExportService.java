package naumen.java.project.service.export.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.export.ExportResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для эспорта аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorAnalyticsExportService {

    private final Map<String, ContractorAnalyticsExporter> exporters;

    /**
     * Зарегистрировать экспортеры
     */
    public ContractorAnalyticsExportService(List<ContractorAnalyticsExporter> exporters) {
        Map<String, ContractorAnalyticsExporter> map = new HashMap<>();

        for (ContractorAnalyticsExporter exporter : exporters) {
            String format = exporter.getSupports();
            if (map.containsKey(format)) {
                throw new IllegalStateException("Дубликат экспортера для формата: " + format);
            }
            map.put(format, exporter);
        }

        this.exporters = map;
    }

    /**
     * Проверить на возможность экспорта в указанный формат
     */
    public boolean isSupported(String format) {
        return exporters.containsKey(format);
    }

    /**
     * Вернуть возможные форматы экспорта
     */
    public String getSupportedFormats() {
        return exporters.keySet().stream()
                .sorted()
                .collect(java.util.stream.Collectors.joining(", "));
    }

    /**
     * Экспорт аналитики в заданный формат
     * @param analytics Готовая аналитика по контрагентам
     * @param baseFilename Название итогового файла
     * @param format Формат итогового файла
     */
    public ExportResult export(ContractorAnalyticsResponse analytics, String baseFilename, String format) {
        ContractorAnalyticsExporter exporter = exporters.get(format);
        if (exporter == null) {
            String available = String.join(", ", exporters.keySet());
            throw new IllegalArgumentException(
                    "Формат экспорта '" + format + "' не поддерживается. Доступные форматы: " + available
            );
        }

        byte[] fileBytes = exporter.export(analytics);
        String filename = baseFilename + exporter.getFileExtension();

        return new ExportResult(fileBytes, filename);
    }
}
