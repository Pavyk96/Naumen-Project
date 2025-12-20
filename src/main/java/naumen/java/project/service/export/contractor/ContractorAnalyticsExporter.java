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
     * Вернуть формат экспорта, который поддерживает реализация
     */
    String getSupports();

    /**
     * Вернуть расширение файла для данного формата
     */
    String getFileExtension();

    /**
     * Сформировать файл экспорта для переданной аналитики
     */
    byte[] export(ContractorAnalyticsResponse analytics);
}
