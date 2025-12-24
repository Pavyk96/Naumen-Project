package naumen.java.project.export;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;
import naumen.java.project.dto.analytics.deal.response.*;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.service.analytics.deal.DealAnalyticsService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Интеграционные тесты для экспорта аналитики сделок
 *
 * @author Daria
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тесты экспорта аналитики Deal")
class DealAnalyticsExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DealAnalyticsService dealAnalyticsService;

    private static DealAnalyticsResponseDTO testAnalyticsResponse;
    private static String testRequestJsonTemplate;

    @BeforeAll
    static void setUpBeforeAll() {
        testAnalyticsResponse = createTestAnalyticsResponse();
        testRequestJsonTemplate = createTestRequestJsonTemplate();
    }

    private static DealAnalyticsResponseDTO createTestAnalyticsResponse() {
        DealAnalyticsPortfolioSummary summary = new DealAnalyticsPortfolioSummary(
                6L,
                2L,
                0.0,
                357.0
        );

        List<BreakdownData> typeStatusData = List.of(
                new BreakdownData(
                        null,
                        null,
                        DealType.CREDIT.getDisplayName(),
                        DealStatus.ACTIVE.getDisplayName(),
                        new DealAnalyticsMetrics(2L, 0.3333333333333333, 357.0)
                ),
                new BreakdownData(
                        null,
                        null,
                        DealType.LEASING.getDisplayName(),
                        DealStatus.DRAFT.getDisplayName(),
                        new DealAnalyticsMetrics(4L, 0.0, 357.0)
                )
        );

        List<BreakdownData> timeIndustryData = List.of(
                new BreakdownData(
                        new DealAnalyticsPeriodInfo(2025, 1),
                        new DealAnalyticsIndustryInfo(1L, "IT"),
                        null,
                        null,
                        new DealAnalyticsMetrics(1L, 0.16666666666666666, 357.0)
                ),
                new BreakdownData(
                        new DealAnalyticsPeriodInfo(2025, 3),
                        new DealAnalyticsIndustryInfo(2L, "Транспорт"),
                        null,
                        null,
                        new DealAnalyticsMetrics(2L, 0.3333333333333333, 357.0)
                ),
                new BreakdownData(
                        new DealAnalyticsPeriodInfo(2025, 3),
                        new DealAnalyticsIndustryInfo(1L, "IT"),
                        null,
                        null,
                        new DealAnalyticsMetrics(3L, 0.5, 357.0)
                )
        );

        List<DealAnalyticsBreakdown> breakdowns = List.of(
                new DealAnalyticsBreakdown("typeStatus", typeStatusData),
                new DealAnalyticsBreakdown("timeIndustry", timeIndustryData)
        );

        List<DealAnalyticsFunnelStage> funnelStages = List.of(
                new DealAnalyticsFunnelStage("Черновик", 4L, 0.6666666666666666, 357.0),
                new DealAnalyticsFunnelStage("Активная", 2L, 0.3333333333333333, 357.0),
                new DealAnalyticsFunnelStage("На рассмотрении", 0L, 0.0, 0.0),
                new DealAnalyticsFunnelStage("Утвержденная", 0L, 0.0, 0.0),
                new DealAnalyticsFunnelStage("Закрыта", 0L, 0.0, 0.0)
        );

        DealAnalyticsFunnelAnalysis funnelAnalysis = new DealAnalyticsFunnelAnalysis(funnelStages, 0.0);

        return new DealAnalyticsResponseDTO(summary, breakdowns, funnelAnalysis);
    }

    private static String createTestRequestJsonTemplate() {
        return """
                {
                  "exportConfig": {
                    "exportFormat": "%s",
                    "filename": "deal_analytics_2024"
                  },
                  "dimensions": ["typeStatus", "timeIndustry"],
                  "metrics": ["count", "successRate", "durationDays"],
                  "filters": {
                    "types": [],
                    "statuses": [],
                    "dateRange": {
                      "openedAt": {"from": "2023-01-01", "to": "2026-12-31"},
                      "agreementDate": {"from": "2023-01-01", "to": "2026-12-31"}
                    }
                  },
                  "includeFunnel": true
                }
                """;
    }

    /**
     * Экспорт аналитики сделок в PDF
     */
    @Test
    @DisplayName("Проверка экспорта формата PDF")
    void exportDealAnalyticsToPdf() throws Exception {
        Mockito.when(dealAnalyticsService.analyze(
                Mockito.any(DealAnalyticsFilter.class),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.eq(true)
        )).thenReturn(testAnalyticsResponse);

        String requestJson = String.format(testRequestJsonTemplate, "PDF");

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/deal/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION,
                                Matchers.containsString("attachment; filename=\"deal_analytics_2024.pdf\"")))
                .andReturn();

        byte[] pdfBytes = result.getResponse().getContentAsByteArray();

        Assertions.assertTrue(pdfBytes.length > 0);
        Assertions.assertEquals('%', (char) pdfBytes[0]);
        Assertions.assertEquals('P', (char) pdfBytes[1]);
        Assertions.assertEquals('D', (char) pdfBytes[2]);
        Assertions.assertEquals('F', (char) pdfBytes[3]);

        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            String normalizedText = text.replaceAll("\\s+", " ");

            Assertions.assertTrue(normalizedText.contains("Аналитика по сделкам"));
            Assertions.assertTrue(normalizedText.contains("Общая статистика"));
            Assertions.assertTrue(normalizedText.contains("Всего сделок: 6"));
            Assertions.assertTrue(normalizedText.contains("Активных сделок: 2"));
            Assertions.assertTrue(normalizedText.contains("Процент успешных: 0,00%") ||
                    normalizedText.contains("Процент успешных: 0.00%"));
            Assertions.assertTrue(normalizedText.contains("Средняя длительность: 357,0 дней") ||
                    normalizedText.contains("Средняя длительность: 357.0 дней"));
            Assertions.assertTrue(normalizedText.contains("Детализация"));
            Assertions.assertTrue(normalizedText.contains("Разрез: typeStatus"));
            Assertions.assertTrue(normalizedText.contains("Разрез: timeIndustry"));
            Assertions.assertTrue(normalizedText.contains("Тип: Кредитная сделка, Статус: Активная"));
            Assertions.assertTrue(normalizedText.contains("Количество: 2") && normalizedText.contains("Успешность: 33,33%"));
            Assertions.assertTrue(normalizedText.contains("Тип: Лизинговая сделка, Статус: Черновик"));
            Assertions.assertTrue(normalizedText.contains("Количество: 4") && normalizedText.contains("Успешность: 0,00%"));
            Assertions.assertTrue(normalizedText.contains("Период: 2025 Q1") && normalizedText.contains("Отрасль: IT"));
            Assertions.assertTrue(normalizedText.contains("Количество: 1") && normalizedText.contains("Успешность: 16,67%"));
            Assertions.assertTrue(normalizedText.contains("Период: 2025 Q3") && normalizedText.contains("Отрасль: Транспорт"));
            Assertions.assertTrue(normalizedText.contains("Количество: 2") && normalizedText.contains("Успешность: 33,33%"));
            Assertions.assertTrue(normalizedText.contains("Период: 2025 Q3") && normalizedText.contains("Отрасль: IT"));
            Assertions.assertTrue(normalizedText.contains("Количество: 3") && normalizedText.contains("Успешность: 50,00%"));
            Assertions.assertTrue(normalizedText.contains("Воронка"));
            Assertions.assertTrue(normalizedText.contains("Средний цикл сделки: 0,0 дней") ||
                    normalizedText.contains("Средний цикл сделки: 0.0 дней"));
            Assertions.assertTrue(normalizedText.contains("Этап: Черновик"));
            Assertions.assertTrue(normalizedText.contains("Количество: 4") &&
                    normalizedText.contains("Конверсия: 66,67%") &&
                    normalizedText.contains("Средняя длительность: 357,0 дней"));
            Assertions.assertTrue(normalizedText.contains("Этап: Активная"));
            Assertions.assertTrue(normalizedText.contains("Количество: 2") &&
                    normalizedText.contains("Конверсия: 33,33%") &&
                    normalizedText.contains("Средняя длительность: 357,0 дней"));
            Assertions.assertTrue(normalizedText.contains("Этап: На рассмотрении"));
            Assertions.assertTrue(normalizedText.contains("Количество: 0") &&
                    normalizedText.contains("Конверсия: 0,00%"));
            Assertions.assertTrue(normalizedText.contains("Этап: Утвержденная"));
            Assertions.assertTrue(normalizedText.contains("Этап: Закрыта"));

        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга файла PDF", e);
        }
    }

    /**
     * Экспорт аналитики сделок в XLSX
     */
    @Test
    @DisplayName("Проверка экспорта формата XLSX")
    void exportDealAnalyticsToXlsx() throws Exception {
        Mockito.when(dealAnalyticsService.analyze(
                Mockito.any(DealAnalyticsFilter.class),
                Mockito.anyList(),
                Mockito.anyList(),
                Mockito.eq(true)
        )).thenReturn(testAnalyticsResponse);

        String requestJson = String.format(testRequestJsonTemplate, "XLSX");

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/deal/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION,
                                Matchers.containsString("attachment; filename=\"deal_analytics_2024.xlsx\"")))
                .andReturn();

        byte[] xlsxBytes = result.getResponse().getContentAsByteArray();

        Assertions.assertTrue(xlsxBytes.length > 0);
        Assertions.assertEquals(0x50, xlsxBytes[0] & 0xFF);
        Assertions.assertEquals(0x4B, xlsxBytes[1] & 0xFF);
        Assertions.assertEquals(0x03, xlsxBytes[2] & 0xFF);
        Assertions.assertEquals(0x04, xlsxBytes[3] & 0xFF);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(xlsxBytes))) {
            Sheet summarySheet = workbook.getSheet("Общая статистика");
            Assertions.assertNotNull(summarySheet);

            Row summaryHeader = summarySheet.getRow(0);
            Assertions.assertEquals("Всего сделок", summaryHeader.getCell(0).getStringCellValue());
            Assertions.assertEquals("Активных сделок", summaryHeader.getCell(1).getStringCellValue());
            Assertions.assertEquals("Процент успешных", summaryHeader.getCell(2).getStringCellValue());
            Assertions.assertEquals("Средняя длительность (дней)", summaryHeader.getCell(3).getStringCellValue());

            Row summaryData = summarySheet.getRow(1);
            Assertions.assertEquals(6, summaryData.getCell(0).getNumericCellValue());
            Assertions.assertEquals(2, summaryData.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.0, summaryData.getCell(2).getNumericCellValue());
            Assertions.assertEquals(357.0, summaryData.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", summaryData.getCell(2).getCellStyle().getDataFormatString());

            Sheet breakdownSheet = workbook.getSheet("Детализация");
            Assertions.assertNotNull(breakdownSheet);

            Row breakdownHeader = breakdownSheet.getRow(0);
            Assertions.assertEquals("Разрез", breakdownHeader.getCell(0).getStringCellValue());
            Assertions.assertEquals("Тип", breakdownHeader.getCell(1).getStringCellValue());
            Assertions.assertEquals("Статус", breakdownHeader.getCell(2).getStringCellValue());
            Assertions.assertEquals("Год", breakdownHeader.getCell(3).getStringCellValue());
            Assertions.assertEquals("Квартал", breakdownHeader.getCell(4).getStringCellValue());
            Assertions.assertEquals("Отрасль", breakdownHeader.getCell(5).getStringCellValue());
            Assertions.assertEquals("Количество", breakdownHeader.getCell(6).getStringCellValue());
            Assertions.assertEquals("Успешность", breakdownHeader.getCell(7).getStringCellValue());
            Assertions.assertEquals("Длительность (дней)", breakdownHeader.getCell(8).getStringCellValue());

            boolean foundCreditActive = false;
            boolean foundLeasingDraft = false;
            boolean foundQ1IT = false;
            boolean foundQ3Transport = false;
            boolean foundQ3IT = false;

            for (int i = 1; i <= breakdownSheet.getLastRowNum(); i++) {
                Row row = breakdownSheet.getRow(i);
                if (row == null) continue;

                String dimension = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                String type = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : "";
                String status = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : "";
                double year = row.getCell(3) != null ? row.getCell(3).getNumericCellValue() : 0;
                double quarter = row.getCell(4) != null ? row.getCell(4).getNumericCellValue() : 0;
                String industry = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "";
                double count = row.getCell(6) != null ? row.getCell(6).getNumericCellValue() : 0;
                double successRate = row.getCell(7) != null ? row.getCell(7).getNumericCellValue() : 0;
                double duration = row.getCell(8) != null ? row.getCell(8).getNumericCellValue() : 0;

                if ("typeStatus".equals(dimension) && "Кредитная сделка".equals(type) && "Активная".equals(status)) {
                    foundCreditActive = true;
                    Assertions.assertEquals(2, count);
                    Assertions.assertEquals(0.3333333333333333, successRate, 0.0001);
                    Assertions.assertEquals(357.0, duration);
                    Assertions.assertEquals("0.00%", row.getCell(7).getCellStyle().getDataFormatString());
                }

                if ("typeStatus".equals(dimension) && "Лизинговая сделка".equals(type) && "Черновик".equals(status)) {
                    foundLeasingDraft = true;
                    Assertions.assertEquals(4, count);
                    Assertions.assertEquals(0.0, successRate, 0.0001);
                    Assertions.assertEquals(357.0, duration);
                    Assertions.assertEquals("0.00%", row.getCell(7).getCellStyle().getDataFormatString());
                }

                if ("timeIndustry".equals(dimension) && 2025 == year && 1 == quarter && "IT".equals(industry)) {
                    foundQ1IT = true;
                    Assertions.assertEquals(1, count);
                    Assertions.assertEquals(0.16666666666666666, successRate, 0.0001);
                    Assertions.assertEquals(357.0, duration);
                    Assertions.assertEquals("0.00%", row.getCell(7).getCellStyle().getDataFormatString());
                }

                if ("timeIndustry".equals(dimension) && 2025 == year && 3 == quarter && "Транспорт".equals(industry)) {
                    foundQ3Transport = true;
                    Assertions.assertEquals(2, count);
                    Assertions.assertEquals(0.3333333333333333, successRate, 0.0001);
                    Assertions.assertEquals(357.0, duration);
                    Assertions.assertEquals("0.00%", row.getCell(7).getCellStyle().getDataFormatString());
                }

                if ("timeIndustry".equals(dimension) && 2025 == year && 3 == quarter && "IT".equals(industry)) {
                    foundQ3IT = true;
                    Assertions.assertEquals(3, count);
                    Assertions.assertEquals(0.5, successRate, 0.0001);
                    Assertions.assertEquals(357.0, duration);
                    Assertions.assertEquals("0.00%", row.getCell(7).getCellStyle().getDataFormatString());
                }
            }

            Assertions.assertTrue(foundCreditActive);
            Assertions.assertTrue(foundLeasingDraft);
            Assertions.assertTrue(foundQ1IT);
            Assertions.assertTrue(foundQ3Transport);
            Assertions.assertTrue(foundQ3IT);

            Sheet funnelSheet = workbook.getSheet("Воронка");
            Assertions.assertNotNull(funnelSheet);

            Row avgCycleRow = funnelSheet.getRow(0);
            Assertions.assertEquals("Средний цикл (дней):", avgCycleRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(0.0, avgCycleRow.getCell(1).getNumericCellValue());

            Row funnelHeader = funnelSheet.getRow(2);
            Assertions.assertEquals("Этап", funnelHeader.getCell(0).getStringCellValue());
            Assertions.assertEquals("Количество", funnelHeader.getCell(1).getStringCellValue());
            Assertions.assertEquals("Конверсия", funnelHeader.getCell(2).getStringCellValue());
            Assertions.assertEquals("Средняя длительность (дней)", funnelHeader.getCell(3).getStringCellValue());

            Row draftRow = funnelSheet.getRow(3);
            Assertions.assertEquals("Черновик", draftRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(4, draftRow.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.6666666666666666, draftRow.getCell(2).getNumericCellValue());
            Assertions.assertEquals(357.0, draftRow.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", draftRow.getCell(2).getCellStyle().getDataFormatString());

            Row activeRow = funnelSheet.getRow(4);
            Assertions.assertEquals("Активная", activeRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(2, activeRow.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.3333333333333333, activeRow.getCell(2).getNumericCellValue());
            Assertions.assertEquals(357.0, activeRow.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", activeRow.getCell(2).getCellStyle().getDataFormatString());

            Row reviewRow = funnelSheet.getRow(5);
            Assertions.assertEquals("На рассмотрении", reviewRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(0, reviewRow.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.0, reviewRow.getCell(2).getNumericCellValue());
            Assertions.assertEquals(0.0, reviewRow.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", reviewRow.getCell(2).getCellStyle().getDataFormatString());

            Row approvedRow = funnelSheet.getRow(6);
            Assertions.assertEquals("Утвержденная", approvedRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(0, approvedRow.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.0, approvedRow.getCell(2).getNumericCellValue());
            Assertions.assertEquals(0.0, approvedRow.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", approvedRow.getCell(2).getCellStyle().getDataFormatString());

            Row closedRow = funnelSheet.getRow(7);
            Assertions.assertEquals("Закрыта", closedRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(0, closedRow.getCell(1).getNumericCellValue());
            Assertions.assertEquals(0.0, closedRow.getCell(2).getNumericCellValue());
            Assertions.assertEquals(0.0, closedRow.getCell(3).getNumericCellValue());
            Assertions.assertEquals("0.00%", closedRow.getCell(2).getCellStyle().getDataFormatString());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка парсинга файла XLSX", e);
        }
    }

    /**
     * Экспорт аналитики сделок c невалидным форматом
     */
    @Test
    @DisplayName("Проверка невалидного формата")
    void exportInvalidFormat() throws Exception {
        String requestJson = String.format(testRequestJsonTemplate, "INVALID");

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/analytics/deal/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isBadRequest())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
                        .value(Matchers.containsString("Формат экспорта 'INVALID' не поддерживается.")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.path")
                        .value("/analytics/deal/export"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("UNSUPPORTED_EXPORT_FORMAT"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.timestamp").exists());
    }
}
