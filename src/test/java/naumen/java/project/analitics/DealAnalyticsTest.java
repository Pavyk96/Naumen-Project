package naumen.java.project.analitics;

import naumen.java.project.model.*;
import naumen.java.project.repository.DealRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Интеграционные тесты для аналитики сделок
 *
 * @author Daria
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тесты аналитики Deal")
class DealAnalyticsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DealRepository dealRepository;

    /**
     * Проверка работы анализа сделок с валидным json
     * Deal с разными типами и статусами (некоторые статусы повторяются)
     */
    @DisplayName("Тест с валидным json")
    @Test
    void analyzeDealTest() throws Exception {
        Country country = new Country("RU", "Россия");
        OrgForm orgForm = new OrgForm("АО", "Акционерное общество");

        Industry industry1 = new Industry(1L, "IT");
        Industry industry2 = new Industry(2L, "Траснпорт");

        Contractor contractor1 = new Contractor("Авто мир", country, industry1, orgForm);
        Contractor contractor2 = new Contractor("Синара-Транспортные Машины", country, industry2, orgForm);

        Deal dealActive = new Deal(
                UUID.randomUUID(),
                "Описание активной сделки",
                "1",
                LocalDate.parse("2025-09-01"),
                LocalDateTime.parse("2025-09-01T10:00:00"),
                null,
                DealType.CREDIT,
                DealStatus.ACTIVE
        );
        dealActive.addContractor(contractor1);

        Deal dealWon = new Deal(
                UUID.randomUUID(),
                "Описание выигранной сделки",
                "2",
                LocalDate.parse("2025-06-01"),
                LocalDateTime.parse("2025-06-01T10:00:00"),
                LocalDateTime.parse("2025-07-01T10:00:00"),
                DealType.LEASING,
                DealStatus.WON
        );
        dealWon.addContractor(contractor2);

        Deal dealDraft1 = new Deal(
                UUID.randomUUID(),
                "Описание черновика",
                "3",
                LocalDate.parse("2025-07-01"),
                LocalDateTime.parse("2025-07-01T10:00:00"),
                null,
                DealType.LEASING,
                DealStatus.DRAFT
        );

        Deal dealDraft2 = new Deal(
                UUID.randomUUID(),
                "Описание черновика",
                "4",
                LocalDate.parse("2025-10-01"),
                LocalDateTime.parse("2025-10-01T10:00:00"),
                null,
                DealType.LEASING,
                DealStatus.DRAFT
        );

        List<Deal> deals = List.of(
                dealActive,
                dealWon,
                dealDraft1,
                dealDraft2
        );

        Mockito.when(dealRepository.findDealsWithFilters(
                Mockito.eq(List.of(DealType.CREDIT, DealType.LEASING)),
                Mockito.eq(List.of(DealStatus.ACTIVE, DealStatus.WON, DealStatus.DRAFT)),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class)
        )).thenReturn(deals);

        String requestJson = """
                {
                  "dimensions": ["typeStatus", "timeIndustry"],
                  "metrics": ["count", "successRate", "durationDays"],
                  "filters": {
                    "types": ["CREDIT", "LEASING"],
                    "statuses": ["ACTIVE", "WON", "DRAFT"],
                    "dateRange": {
                      "openedAt": {"from": "2025-01-01", "to": "2025-12-31"},
                      "agreementDate": {"from": "2025-01-01", "to": "2025-12-31"}
                    }
                  },
                  "includeFunnel": true
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/deal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.portfolioSummary").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.portfolioSummary.totalDeals").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.portfolioSummary.activeDeals").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.portfolioSummary.winRate").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.portfolioSummary.avgRealDuration").value(95))

                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown").isArray())

                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].dimension").value("typeStatus"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].type").value("Кредитная сделка"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].status").value("Активная"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].metrics.count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].metrics.successRate").value(1.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].metrics.durationDays").value(106))

                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].dimension").value("timeIndustry"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].period.year").value(2025))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].period.quarter").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].industry.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].industry.name").value("Траснпорт"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].metrics.count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].metrics.successRate").value(1.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].metrics.durationDays").value(30))

                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].period.year").value(2025))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].period.quarter").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].industry.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].industry.name").value("IT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].metrics.count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].metrics.successRate").value(1.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[1].metrics.durationDays").value(106))

                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages").isArray())

                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[0].stage").value("Черновик"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[0].count").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[0].conversionRate").value(2.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[0].avgDurationDays").value(122))

                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[1].stage").value("Активная"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[1].count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[1].conversionRate").value(1.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[1].avgDurationDays").value(106))

                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[3].stage").value("Утвержденная"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[3].count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[3].conversionRate").value(1.0 / 4.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.stages[3].avgDurationDays").value(30))

                .andExpect(MockMvcResultMatchers.jsonPath("$.funnelAnalysis.avgSalesCycle").value(30.0));

        Mockito.verify(dealRepository).findDealsWithFilters(
                Mockito.eq(List.of(DealType.CREDIT, DealType.LEASING)),
                Mockito.eq(List.of(DealStatus.ACTIVE, DealStatus.WON, DealStatus.DRAFT)),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDate.class),
                Mockito.any(LocalDate.class)
        );
    }

    /**
     * Проверяем, что при неправильной дате, выводится сообщение об ошибке
     */
    @DisplayName("Тест с невалидной датой")
    @Test
    void notValidDateTest() throws Exception {
        String invalidJson = """
                {
                  "dimensions": ["typeStatus", "timeIndustry"],
                  "metrics": ["count", "successRate", "durationDays"],
                  "filters": {
                    "types": ["CREDIT", "LEASING"],
                    "statuses": ["ACTIVE", "WON", "DRAFT"],
                    "dateRange": {
                      "openedAt": {"from": "not-valid", "to": "2025-12-31"},
                      "agreementDate": {"from": "2025-01-01", "to": "2025-12-31"}
                    }
                  },
                  "includeFunnel": true
                }
            """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/deal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Ошибка валидации данных: filters.dateRange.openedAt.from: " +
                                        "Поле from должно быть в формате ГГГГ-ММ-ДД " +
                                        "с допустимыми месяцами (01-12) и днями (01-31)."
                        )));
    }

    /**
     * Проверяем, что при несуществующем enum, выводится сообщение об ошибке
     */
    @DisplayName("Тест с невалидным enum")
    @Test
    void notValidEnumTest() throws Exception {
        String invalidJson = """
                {
                  "dimensions": ["typeStatus", "timeIndustry"],
                  "metrics": ["count", "successRate", "durationDays"],
                  "filters": {
                    "types": ["CREDIT", "not-valid"],
                    "statuses": ["ACTIVE", "WON", "DRAFT"],
                    "dateRange": {
                      "openedAt": {"from": "2025-01-01", "to": "2025-12-31"},
                      "agreementDate": {"from": "2025-01-01", "to": "2025-12-31"}
                    }
                  },
                  "includeFunnel": true
                }
            """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/deal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "Ошибка валидации данных: filters.types[1]: " +
                                        "Значение 'not-valid' не существует для DealType. " +
                                        "Доступные значения: CREDIT, DEBIT, LEASING"
                        )));
    }
}
