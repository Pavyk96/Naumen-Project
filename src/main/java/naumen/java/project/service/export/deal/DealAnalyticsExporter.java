package naumen.java.project.service.export.deal;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.export.ExportFormat;

/**
 * Интерфейс экспортера аналитики по сделкам
 *
 * @author Daria
 */
public interface DealAnalyticsExporter {

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
    byte[] export(DealAnalyticsResponseDTO analytics);
}
