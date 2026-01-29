package naumen.java.project.service.export.deal;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.dto.export.ExportFile;
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
    ExportFormat getSupport();

    /**
     * Сформировать файл экспорта для переданной аналитики
     */
    ExportFile export(String baseFilename, DealAnalyticsResponseDTO analytics);
}
