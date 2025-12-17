package naumen.java.project.service.export.deal;

import naumen.java.project.dto.analytics.deal.response.*;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownTimeIndustry;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownTypeStatus;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.dto.export.ExportFormat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Сервис для экспорта аналитики по сделкам в формате PDF
 *
 * @author Daria
 */
@Service
public class DealAnalyticsPdfExporter implements DealAnalyticsExporter {

    @Override
    public ExportFormat supports() {
        return ExportFormat.PDF;
    }

    @Override
    public String fileExtension() {
        return ".pdf";
    }

    @Override
    public byte[] export(DealAnalyticsResponseDTO analytics) {
        if (analytics == null) {
            throw new IllegalArgumentException("Данные для экспорта не могут быть пустыми");
        }
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

                    line(cs, "Аналитика по сделкам");
                    line(cs, "");

                    writeSummary(cs, analytics.portfolioSummary());
                    line(cs, "");
                    writeBreakdowns(cs, analytics.breakdown());
                    line(cs, "");
                    writeFunnelAnalysis(cs, analytics.funnelAnalysis());
                } finally {
                    cs.endText();
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка формирования PDF отчета", e);
        }
    }

    /**
     * Загружает TTF шрифт для корректного отображения кириллицы
     *
     * @throws IllegalStateException если файл шрифта не найден по пути /fonts/DejaVuSans.ttf в ресурсах
     * @throws IOException если файл шрифта поврежден или не может быть прочитан
     */
    private PDType0Font loadFont(PDDocument document) throws IOException {
        try (InputStream fontStream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            if (fontStream == null) {
                throw new IllegalStateException("Критическая ошибка: шрифт /fonts/DejaVuSans.ttf не найден в ресурсах приложения");
            }
            return PDType0Font.load(document, fontStream);
        }
    }

    /**
     * Отрисовывает блок сводной статистики
     *
     * @throws IOException если произошла ошибка записи в поток контента страницы
     */
    private void writeSummary(PDPageContentStream cs, DealAnalyticsPortfolioSummary summary) throws IOException {
        if (summary != null) {
            line(cs, "Общая статистика");
            line(cs, "Всего сделок: " + summary.totalDeals());
            line(cs, "Активных сделок: " + summary.activeDeals());
            line(cs, "Процент успешных: " + String.format("%.2f%%", summary.winRate() * 100));
            line(cs, "Средняя длительность: " + String.format("%.1f дней", summary.avgRealDuration()));
        }
    }

    /**
     * Отрисовывает списки детализации данных.
     *
     * @throws IOException если произошла ошибка записи в поток контента страницы
     */
    private void writeBreakdowns(PDPageContentStream cs, List<DealAnalyticsBreakdown> breakdowns) throws IOException {
        if (breakdowns != null) {
            line(cs, "Детализация");

            for (DealAnalyticsBreakdown breakdown : breakdowns) {
                if (breakdown.dimension() == null || breakdown.data() == null) {
                    continue;
                }

                line(cs, "Разрез: " + breakdown.dimension());

                for (BreakdownData data : breakdown.data()) {
                    if (data == null) continue;

                    if (data instanceof BreakdownTypeStatus typeStatusData) {
                        line(cs, "  Тип: " + typeStatusData.type() + ", Статус: " + typeStatusData.status());
                        if (typeStatusData.metrics() != null) {
                            writeMetrics(cs, typeStatusData.metrics(), 4);
                        }
                    } else if (data instanceof BreakdownTimeIndustry timeIndustryData) {
                        String periodStr = (timeIndustryData.period() != null)
                                ? timeIndustryData.period().year() + " Q" + timeIndustryData.period().quarter()
                                : "н/д";

                        String industryStr = (timeIndustryData.industry() != null)
                                ? timeIndustryData.industry().name()
                                : "н/д";

                        line(cs, "  Период: " + periodStr);
                        line(cs, "  Отрасль: " + industryStr);

                        if (timeIndustryData.metrics() != null) {
                            writeMetrics(cs, timeIndustryData.metrics(), 4);
                        }
                    }
                    line(cs, "");
                }
                line(cs, "");
            }
        }
    }


    /**
     * Отрисовывает этапы воронки
     *
     * @throws IOException если произошла ошибка записи в поток контента страницы
     */
    private void writeFunnelAnalysis(PDPageContentStream cs, DealAnalyticsFunnelAnalysis funnel) throws IOException {
        if (funnel != null) {
            line(cs, "Воронка");
            line(cs, "Средний цикл сделки: " + String.format("%.1f дней", funnel.avgSalesCycle()));
            line(cs, "");

            for (DealAnalyticsFunnelStage stage : funnel.stages()) {
                line(cs, "Этап: " + stage.stage());
                line(cs, "  Количество: " + stage.count());
                line(cs, "  Конверсия: " + String.format("%.2f%%", stage.conversionRate() * 100));
                if (stage.avgDurationDays() > 0) {
                    line(cs, "  Средняя длительность: " + String.format("%.1f дней", stage.avgDurationDays()));
                }
                line(cs, "");
            }
        }
    }

    /**
     * Отрисовывает метрики с заданным отступом
     *
     * @throws IOException если произошла ошибка записи в поток контента страницы
     */
    private void writeMetrics(PDPageContentStream cs, DealAnalyticsMetrics metrics, int indent) throws IOException {
        String indentStr = " ".repeat(indent);
        if (metrics.count() != null) {
            line(cs, indentStr + "Количество: " + metrics.count());
        }
        if (metrics.successRate() != null) {
            line(cs, indentStr + "Успешность: " + String.format("%.2f%%", metrics.successRate() * 100));
        }
        if (metrics.durationDays() != null) {
            line(cs, indentStr + "Длительность: " + String.format("%.1f дней", metrics.durationDays()));
        }
    }

    /**
     * Вспомогательный метод для печати текста с последующим переносом строки
     *
     * @throws IOException если невозможно отобразить текст
     */
    private void line(PDPageContentStream cs, String text) throws IOException {
        cs.showText(text);
        cs.newLine();
    }
}