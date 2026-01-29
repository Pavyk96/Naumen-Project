package naumen.java.project.service.export.contractor.exportImpl;

import naumen.java.project.dto.analytics.contractor.response.*;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExporter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Экспортер аналитики контрагентов в XLSX
 *
 * @author Daniil
 */
@Service
public class ContractorAnalyticsXlsxExporter implements ContractorAnalyticsExporter {

    @Override
    public ExportFormat getSupport() {
        return ExportFormat.XLSX;
    }

    @Override
    public ExportFile export(String baseFilename, ContractorAnalyticsResponse analytics) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            writeSummarySheet(workbook, analytics.summary());
            writeBreakdownsSheet(workbook, analytics.breakdown());

            if (analytics.trends() != null) {
                writeTrendsSheet(workbook, analytics.trends());
            }

            workbook.write(out);

            return new ExportFile(baseFilename, out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка формирования XLSX отчета", e);
        }
    }

    /**
     * Записать сводные метрики на отдельный лист
     *
     * @param workbook workbook, в который добавляется лист
     * @param summary сводные метрики
     */
    private void writeSummarySheet(XSSFWorkbook workbook, ContractorAnalyticsSummary summary) {
        Sheet sheet = workbook.createSheet("Summary");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("total_contractors");
        header.createCell(1).setCellValue("avg_deals_per_contractor");
        header.createCell(2).setCellValue("active_contractors");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(summary.totalContractors());
        row.createCell(1).setCellValue(summary.avgDealsPerContractor());
        row.createCell(2).setCellValue(summary.activeContractors());

        autosize(sheet, 3);
    }

    /**
     * Записать разрезы аналитики на отдельный лист
     *
     * @param workbook workbook, в который добавляется лист
     * @param breakdowns список разрезов
     */
    private void writeBreakdownsSheet(XSSFWorkbook workbook, java.util.List<ContractorAnalyticsBreakdown> breakdowns) {
        Sheet sheet = workbook.createSheet("Breakdowns");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("dimension");
        header.createCell(1).setCellValue("group");
        header.createCell(2).setCellValue("count");
        header.createCell(3).setCellValue("active_deals_count");

        int rowIndex = 1;
        for (ContractorAnalyticsBreakdown breakdown : breakdowns) {
            for (ContractorAnalyticsBreakdownData data : breakdown.data()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(breakdown.dimension());
                row.createCell(1).setCellValue(stringifyGroup(data.group()));

                ContractorAnalyticsMetrics metrics = data.metrics();
                if (metrics != null && metrics.count() != null) {
                    row.createCell(2).setCellValue(metrics.count());
                }
                if (metrics != null && metrics.activeDealsCount() != null) {
                    row.createCell(3).setCellValue(metrics.activeDealsCount());
                }
            }
        }

        autosize(sheet, 4);
    }

    /**
     * Записать тренды по периодам на отдельный лист
     *
     * @param workbook workbook, в который добавляется лист
     * @param trends тренды аналитики
     */
    private void writeTrendsSheet(XSSFWorkbook workbook, ContractorAnalyticsTrends trends) {
        Sheet sheet = workbook.createSheet("Trends");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("period");
        header.createCell(1).setCellValue("new_contractors");

        int rowIndex = 1;
        for (ContractorMonthlyGrowth growth : trends.monthlyGrowth()) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(growth.period());
            row.createCell(1).setCellValue(growth.newContractors());
        }

        autosize(sheet, 2);
    }

    /**
     * Преобразовать группу (map ключ-значение) в строку вида key=value
     *
     * @param group группа измерений
     * @return строковое представление группы
     */
    private String stringifyGroup(Map<String, Object> group) {
        if (group == null || group.isEmpty()) {
            return "";
        }
        return group.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
    }

    /**
     * Автоматически подогнать ширину колонок
     *
     * @param sheet лист, для которого выполняется autosize
     * @param columns количество колонок
     */
    private void autosize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
