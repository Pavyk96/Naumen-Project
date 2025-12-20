package naumen.java.project.service.export.contractor;

import naumen.java.project.dto.analytics.contractor.response.*;
import naumen.java.project.dto.export.ExportFormat;
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
 * @author Daniil Mezev
 */
@Service
public class ContractorAnalyticsXlsxExporter implements ContractorAnalyticsExporter {

    @Override
    public String getSupports() {
        return "XLSX";
    }

    @Override
    public String getFileExtension() {
        return ".xlsx";
    }

    @Override
    public byte[] export(ContractorAnalyticsResponse analytics) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            writeSummarySheet(workbook, analytics.summary());
            writeBreakdownsSheet(workbook, analytics.breakdown());

            if (analytics.trends() != null) {
                writeTrendsSheet(workbook, analytics.trends());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка формирования XLSX отчета", e);
        }
    }

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

    private String stringifyGroup(Map<String, Object> group) {
        if (group == null || group.isEmpty()) {
            return "";
        }
        return group.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
    }

    private void autosize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
