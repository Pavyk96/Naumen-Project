package naumen.java.project.service.export.contractor.exportImpl;

import naumen.java.project.dto.analytics.contractor.response.*;
import naumen.java.project.dto.export.ExportFile;
import naumen.java.project.dto.export.ExportFormat;
import naumen.java.project.service.export.contractor.ContractorAnalyticsExporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Экспортер аналитики контрагентов в PDF.
 *
 * @author Daniil
 */
@Service
public class ContractorAnalyticsPdfExporter implements ContractorAnalyticsExporter {

    @Override
    public ExportFormat getSupport() {
        return ExportFormat.PDF;
    }

    @Override
    public ExportFile export(String baseFilename, ContractorAnalyticsResponse analytics) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType0Font font = loadFont(document);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.beginText();
                try {
                    cs.setFont(font, 12);
                    cs.setLeading(14f);
                    cs.newLineAtOffset(50, 770);

                    line(cs, "Contractor analytics report");
                    line(cs, "");

                    writeSummary(cs, analytics.summary());
                    line(cs, "");
                    writeBreakdowns(cs, analytics.breakdown());

                    if (analytics.trends() != null) {
                        line(cs, "");
                        writeTrends(cs, analytics.trends());
                    }
                } finally {
                    cs.endText();
                }
            }

            document.save(out);

            return new ExportFile(baseFilename, out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка формирования PDF отчета", e);
        }
    }

    /**
     * Загрузить Unicode-шрифт из ресурсов приложения
     *
     * @param document PDF документ
     * @return загруженный шрифт
     * @throws IOException ошибка чтения шрифта
     */
    private PDType0Font loadFont(PDDocument document) throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            if (fontStream == null) {
                throw new IllegalStateException("Шрифт не найден: /fonts/DejaVuSans.ttf");
            }
            return PDType0Font.load(document, fontStream);
        }
    }

    /**
     * Записать блок сводных метрик в PDF
     *
     * @param cs поток контента страницы
     * @param summary сводные метрики
     * @throws IOException ошибка записи в PDF
     */
    private void writeSummary(PDPageContentStream cs, ContractorAnalyticsSummary summary) throws IOException {
        line(cs, "Summary");
        line(cs, "total_contractors: " + summary.totalContractors());
        line(cs, "avg_deals_per_contractor: " + summary.avgDealsPerContractor());
        line(cs, "active_contractors: " + summary.activeContractors());
    }

    /**
     * Записать блок разрезов аналитики в PDF
     *
     * @param cs поток контента страницы
     * @param breakdowns список разрезов
     * @throws IOException ошибка записи в PDF
     */
    private void writeBreakdowns(PDPageContentStream cs, java.util.List<ContractorAnalyticsBreakdown> breakdowns)
            throws IOException {
        line(cs, "Breakdowns");
        for (ContractorAnalyticsBreakdown breakdown : breakdowns) {
            line(cs, "- " + breakdown.dimension());
            for (ContractorAnalyticsBreakdownData data : breakdown.data()) {
                ContractorAnalyticsMetrics metrics = data.metrics();

                String group = stringifyGroup(data.group());
                String count = metrics != null && metrics.count() != null ? metrics.count().toString() : "null";
                String active = metrics != null && metrics.activeDealsCount() != null ? metrics.activeDealsCount().toString() : "null";

                line(cs, "  " + group + " | count=" + count + " | active_deals_count=" + active);
            }
        }
    }

    /**
     * Записать блок трендов аналитики в PDF
     *
     * @param cs поток контента страницы
     * @param trends тренды аналитики
     * @throws IOException ошибка записи в PDF
     */
    private void writeTrends(PDPageContentStream cs, ContractorAnalyticsTrends trends) throws IOException {
        line(cs, "Trends");
        for (ContractorMonthlyGrowth growth : trends.monthlyGrowth()) {
            line(cs, growth.period() + ": " + growth.newContractors());
        }
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
     * Вывести одну строку текста и перейти на новую строку
     *
     * @param cs поток контента страницы
     * @param text текст строки
     * @throws IOException ошибка записи в PDF
     */
    private void line(PDPageContentStream cs, String text) throws IOException {
        cs.showText(text);
        cs.newLine();
    }
}
