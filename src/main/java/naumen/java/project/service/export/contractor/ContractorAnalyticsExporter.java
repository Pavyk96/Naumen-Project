package naumen.java.project.service.export.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;

/**
 * Интерфейс экспортера аналитики по контрагентам
 *
 * @author Daniil
 */
public interface ContractorAnalyticsExporter {
    /**
     * Вернуть формат, который поддерживает экспортер
     */
    ExportFormat getSupport();

    /**
     * Сформировать файл экспорта
     */
    ExportFile export(String baseFilename, ContractorAnalyticsResponse analytics);
}
