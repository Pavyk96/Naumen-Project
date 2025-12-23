package naumen.java.project.service.export.deal.exportImpl;

import naumen.java.project.dto.analytics.deal.response.*;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;
import naumen.java.project.service.export.deal.DealAnalyticsExporter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Сервис для экспорта аналитики по сделкам в формате XLSX
 *
 * @author Daria
 */
@Service
public class DealAnalyticsXlsxExporter implements DealAnalyticsExporter {

    @Override
    public ExportFormat getSupport() {
        return ExportFormat.XLSX;
    }

    @Override
    public ExportFile export(String baseFilename, DealAnalyticsResponseDTO analytics) {
        if (analytics == null) {
            throw new IllegalArgumentException("Данные для экспорта не могут быть null");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            writeSummarySheet(workbook, analytics.portfolioSummary());
            writeBreakdownsSheet(workbook, analytics.breakdown());
            writeFunnelSheet(workbook, analytics.funnelAnalysis());

            workbook.write(out);
            return new ExportFile(baseFilename, out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при формировании XLSX отчета в памяти", e);
        }
    }

    /**
     * Создает лист со сводными метриками портфеля сделок
     */
    private void writeSummarySheet(XSSFWorkbook workbook, DealAnalyticsPortfolioSummary summary) {
        if (summary != null) {
            Sheet sheet = workbook.createSheet("Общая статистика");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Всего сделок");
            header.createCell(1).setCellValue("Активных сделок");
            header.createCell(2).setCellValue("Процент успешных");
            header.createCell(3).setCellValue("Средняя длительность (дней)");

            Row dataRow = sheet.createRow(1);
            dataRow.createCell(0).setCellValue(summary.totalDeals());
            dataRow.createCell(1).setCellValue(summary.activeDeals());
            dataRow.createCell(2).setCellValue(summary.winRate());
            dataRow.createCell(3).setCellValue(summary.avgRealDuration());

            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            dataRow.getCell(2).setCellStyle(percentStyle);

            autosize(sheet, 4);
        }
    }
    /**
     * Создает лист с детализацией по различным измерениям (типы, статусы, отрасли)
     *
     * @throws IllegalStateException если количество данных превышает максимально допустимое количество строк в Excel
     */
    private void writeBreakdownsSheet(XSSFWorkbook workbook, List<DealAnalyticsBreakdown> breakdowns) {
        if (breakdowns == null) {
            return;
        }

        Sheet sheet = workbook.createSheet("Детализация");
        final int MAX_ROWS = 1048576;

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Разрез");
        header.createCell(1).setCellValue("Тип");
        header.createCell(2).setCellValue("Статус");
        header.createCell(3).setCellValue("Год");
        header.createCell(4).setCellValue("Квартал");
        header.createCell(5).setCellValue("Отрасль");
        header.createCell(6).setCellValue("Количество");
        header.createCell(7).setCellValue("Успешность");
        header.createCell(8).setCellValue("Длительность (дней)");

        int rowIndex = 1;
        for (DealAnalyticsBreakdown breakdown : breakdowns) {
            for (BreakdownData data : breakdown.data()) {
                if (rowIndex >= MAX_ROWS) {
                    throw new IllegalStateException("Достигнут лимит строк Excel (1,048,576). Данные детализации слишком велики.");
                }

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(breakdown.dimension());

                if (data.type() != null && data.status() != null) {
                    row.createCell(1).setCellValue(data.type());
                    row.createCell(2).setCellValue(data.status());
                } else {
                    if (data.period() != null) {
                        row.createCell(3).setCellValue(data.period().year());
                        row.createCell(4).setCellValue(data.period().quarter());
                    }
                    if (data.industry() != null) {
                        row.createCell(5).setCellValue(data.industry().name());
                    }
                }
                if (data.metrics() != null) {
                    writeMetricsToRow(row, 6, data.metrics());
                }
            }
        }

        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

        for (int i = 1; i < rowIndex; i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(7) != null) {
                row.getCell(7).setCellStyle(percentStyle);
            }
        }

        autosize(sheet, 9);
    }

    /**
     * Создает лист для анализа воронки продаж с показателями конверсии
     */
    private void writeFunnelSheet(XSSFWorkbook workbook, DealAnalyticsFunnelAnalysis funnel) {
        if (funnel != null) {
            Sheet sheet = workbook.createSheet("Воронка");

            Row header1 = sheet.createRow(0);
            header1.createCell(0).setCellValue("Средний цикл (дней):");
            header1.createCell(1).setCellValue(funnel.avgSalesCycle());

            Row header2 = sheet.createRow(2);
            header2.createCell(0).setCellValue("Этап");
            header2.createCell(1).setCellValue("Количество");
            header2.createCell(2).setCellValue("Конверсия");
            header2.createCell(3).setCellValue("Средняя длительность (дней)");

            int rowIndex = 3;
            for (DealAnalyticsFunnelStage stage : funnel.stages()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(stage.stage());
                row.createCell(1).setCellValue(stage.count());
                row.createCell(2).setCellValue(stage.conversionRate());
                row.createCell(3).setCellValue(stage.avgDurationDays());
            }

            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

            for (int i = 3; i < rowIndex; i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(2) != null) {
                    row.getCell(2).setCellStyle(percentStyle);
                }
            }

            autosize(sheet, 4);
        }
    }

    /**
     * Вспомогательный метод для записи числовых метрик в ячейки строки
     */
    private void writeMetricsToRow(Row row, int startCol, DealAnalyticsMetrics metrics) {
        if (metrics.count() != null) {
            row.createCell(startCol).setCellValue(metrics.count());
        }
        if (metrics.successRate() != null) {
            row.createCell(startCol + 1).setCellValue(metrics.successRate());
        }
        if (metrics.durationDays() != null) {
            row.createCell(startCol + 2).setCellValue(metrics.durationDays());
        }
    }

    /**
     * Устанавливает автоматическую ширину колонок для улучшения читаемости
     */
    private void autosize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
