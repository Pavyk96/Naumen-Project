package naumen.java.project.service.export.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.export.ExportFormat;

/**
 * Интерфейс экспортера аналитики по контрагентам
 *
 * @author Daniil
 */
public interface ContractorAnalyticsExporter {

    /**
     * Формат экспорта, который поддерживает реализация
     */
    ExportFormat supports();

    /**
     * Расширение файла для данного формата (например .pdf, .xlsx).
     */
    String fileExtension();

    /**
     * Сформировать файл экспорта для переданной аналитики
     */
    byte[] export(ContractorAnalyticsResponse analytics);
}
