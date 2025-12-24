package naumen.java.project.export;

import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdownData;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsMetrics;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.dto.analytics.contractor.response.ContractorMonthlyGrowth;
import naumen.java.project.service.analytics.contractor.ContractorAnalyticsService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.Assertions;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


/**
 * Интеграционные тесты для экспорта аналитики
 *
 * @author Daniil Mezev
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContractorAnalyticsExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractorAnalyticsService contractorAnalyticsService;

    private final ContractorAnalyticsResponse analytics;

    public ContractorAnalyticsExportControllerTest() {
        ContractorAnalyticsSummary summary = new ContractorAnalyticsSummary(3L, 1.0d, 3L);
        ContractorAnalyticsBreakdown country = new ContractorAnalyticsBreakdown(
                "country",
                List.of(
                        new ContractorAnalyticsBreakdownData(
                                Map.of("id", "USA", "name", "США"),
                                new ContractorAnalyticsMetrics(2L, 2L)
                        ),
                        new ContractorAnalyticsBreakdownData(
                                Map.of("id", "KZ", "name", "Казахстан"),
                                new ContractorAnalyticsMetrics(1L, 1L)
                        )
                )
        );
        ContractorAnalyticsBreakdown industry = new ContractorAnalyticsBreakdown(
                "industry",
                List.of(
                        new ContractorAnalyticsBreakdownData(
                                Map.of("id", 1L, "name", "IT"),
                                new ContractorAnalyticsMetrics(3L, 3L)
                        )
                )
        );
        ContractorAnalyticsBreakdown orgForm = new ContractorAnalyticsBreakdown(
                "org_form",
                List.of(
                        new ContractorAnalyticsBreakdownData(
                                Map.of("id", "АО", "name", "Акционерное общество"),
                                new ContractorAnalyticsMetrics(3L, 3L)
                        )
                )
        );
        ContractorAnalyticsTrends trends = new ContractorAnalyticsTrends(
                List.of(new ContractorMonthlyGrowth("2025-12", 3L))
        );

        this.analytics = new ContractorAnalyticsResponse(summary, List.of(country, industry, orgForm), trends);
    }

    /**
     * Проверка выгрузки XLSX и ключевых значений в листах
     */
    @Test
    void exportXlsx() throws Exception {
        Mockito.when(contractorAnalyticsService.analyze(
                        ArgumentMatchers.any(ContractorAnalyticsFilters.class),
                        ArgumentMatchers.anyList(),
                        ArgumentMatchers.anyList(),
                        ArgumentMatchers.eq(true)
                ))
                .thenReturn(analytics);

        String requestJson = """
                {
                  "exportConfig": { "exportFormat": "XLSX", "filename": "contractor_analytics" },
                  "dimensions": ["country", "industry", "org_form"],
                  "metrics": ["count", "active_deals_count"],
                  "filters": {
                    "countries": ["KZ", "USA"],
                    "orgForms": ["АО"],
                    "dateRange": {
                      "createDate": { "from": "2024-01-01", "to": "2027-12-31" }
                    }
                  },
                  "includeTrends": true
                }
                """;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/analytics/contractor/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION,
                                Matchers.containsString("attachment; filename=\"contractor_analytics.xlsx\"")))
                .andReturn();

        byte[] bytes = result.getResponse().getContentAsByteArray();
        Assertions.assertTrue(bytes.length > 0);
        Assertions.assertEquals((byte) 0x50, bytes[0]);
        Assertions.assertEquals((byte) 0x4B, bytes[1]);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet summary = workbook.getSheet("Summary");
            Assertions.assertNotNull(summary);

            Row header = summary.getRow(0);
            Assertions.assertEquals("total_contractors", header.getCell(0).getStringCellValue());
            Assertions.assertEquals("avg_deals_per_contractor", header.getCell(1).getStringCellValue());
            Assertions.assertEquals("active_contractors", header.getCell(2).getStringCellValue());

            Row values = summary.getRow(1);
            Assertions.assertEquals(3d, values.getCell(0).getNumericCellValue());
            Assertions.assertEquals(1.0d, values.getCell(1).getNumericCellValue());
            Assertions.assertEquals(3d, values.getCell(2).getNumericCellValue());

            Sheet breakdowns = workbook.getSheet("Breakdowns");
            Assertions.assertNotNull(breakdowns);

            boolean hasKz = false;
            boolean hasUsa = false;

            for (int i = 1; i <= breakdowns.getLastRowNum(); i++) {
                Row r = breakdowns.getRow(i);
                if (r == null) {
                    continue;
                }
                String dim = r.getCell(0) != null ? r.getCell(0).getStringCellValue() : "";
                String group = r.getCell(1) != null ? r.getCell(1).getStringCellValue() : "";

                if ("country".equals(dim) && group.contains("id=KZ")) {
                    hasKz = true;
                    Assertions.assertEquals(1d, r.getCell(2).getNumericCellValue());
                    Assertions.assertEquals(1d, r.getCell(3).getNumericCellValue());
                }
                if ("country".equals(dim) && group.contains("id=USA")) {
                    hasUsa = true;
                    Assertions.assertEquals(2d, r.getCell(2).getNumericCellValue());
                    Assertions.assertEquals(2d, r.getCell(3).getNumericCellValue());
                }
            }

            Assertions.assertTrue(hasKz);
            Assertions.assertTrue(hasUsa);

            Sheet trends = workbook.getSheet("Trends");
            Assertions.assertNotNull(trends);

            Row tRow = trends.getRow(1);
            Assertions.assertEquals("2025-12", tRow.getCell(0).getStringCellValue());
            Assertions.assertEquals(3d, tRow.getCell(1).getNumericCellValue());
        }
    }

    /**
     * Проверка выгрузки PDF и ключевых строк в документе
     */
    @Test
    void exportPdf() throws Exception {
        Mockito.when(contractorAnalyticsService.analyze(
                        ArgumentMatchers.any(ContractorAnalyticsFilters.class),
                        ArgumentMatchers.anyList(),
                        ArgumentMatchers.anyList(),
                        ArgumentMatchers.eq(true)
                ))
                .thenReturn(analytics);

        String requestJson = """
                {
                  "exportConfig": { "exportFormat": "PDF", "filename": "contractor_analytics" },
                  "dimensions": ["country", "industry", "org_form"],
                  "metrics": ["count", "active_deals_count"],
                  "filters": {
                    "countries": ["KZ", "USA"],
                    "orgForms": ["АО"],
                    "dateRange": {
                      "createDate": { "from": "2024-01-01", "to": "2027-12-31" }
                    }
                  },
                  "includeTrends": true
                }
                """;

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/analytics/contractor/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string(HttpHeaders.CONTENT_DISPOSITION,
                                Matchers.containsString("attachment; filename=\"contractor_analytics.pdf\"")))
                .andReturn();

        byte[] bytes = result.getResponse().getContentAsByteArray();
        Assertions.assertTrue(bytes.length > 0);

        String sig = new String(bytes, 0, Math.min(5, bytes.length), StandardCharsets.US_ASCII);
        Assertions.assertTrue(sig.startsWith("%PDF-"));

        try (PDDocument document = PDDocument.load(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Assertions.assertTrue(text.contains("Contractor analytics report"));
            Assertions.assertTrue(text.contains("total_contractors: 3"));
            Assertions.assertTrue(text.contains("id=KZ"));
            Assertions.assertTrue(text.contains("2025-12: 3"));
        }
    }

    /**
     * Проверка обработки ошибки UNSUPPORTED_EXPORT_FORMAT при несуществующем формате
     */
    @Test
    void exportUnsupportedFormat() throws Exception {
        String requestJson = """
            {
              "exportConfig": { "exportFormat": "ZIP", "filename": "contractor_analytics" },
              "dimensions": ["country"],
              "metrics": ["count"],
              "filters": {
                "countries": ["KZ"],
                "dateRange": {
                  "createDate": { "from": "2024-01-01", "to": "2027-12-31" }
                }
              },
              "includeTrends": false
            }
            """;

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/analytics/contractor/export")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(Matchers.containsString("Формат экспорта 'ZIP' не поддерживается")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(Matchers.containsString("Допустимые значения:")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/analytics/contractor/export"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("UNSUPPORTED_EXPORT_FORMAT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());
    }
}
