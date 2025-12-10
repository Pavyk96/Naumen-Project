package naumen.java.project.analitics;

import naumen.java.project.model.*;
import naumen.java.project.repository.ContractorRepository;
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

/**
 * Интеграционные тесты для аналитики
 *
 * @author Daniil Mezev
 */
@SpringBootTest
@AutoConfigureMockMvc
class ContractorAnalyticsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractorRepository contractorRepositoryMock;

    /**
     * Проверяем успешную аналитику, мокаея репозиторий, симулируя
     * возврат контрагентов из бд и проверяем джисон ответа на корректность
     */
    @Test
    void analyzeContractorsTest() throws Exception {
        Country countryKazakhstan = new Country("KZ", "Казахстан");
        Country countryUsa = new Country("USA", "США");

        Industry industryIt = new Industry(1L, "IT");
        OrgForm orgFormAo = new OrgForm("АО", "Акционерное общество");

        Contractor contractorAutoMir = new Contractor("Авто мир", countryKazakhstan, industryIt, orgFormAo);
        contractorAutoMir.setCreateDate(LocalDate.parse("2025-12-09"));

        Contractor contractorNba = new Contractor("NBA", countryUsa, industryIt, orgFormAo);
        contractorNba.setCreateDate(LocalDate.parse("2025-12-11"));

        Contractor contractorNvidea = new Contractor("Nvidea", countryUsa, industryIt, orgFormAo);
        contractorNvidea.setCreateDate(LocalDate.parse("2025-12-09"));

        // - создаём активную сделку и навешиваем на всех троих контрагентов
        Deal activeDeal = new Deal();
        activeDeal.setAgreementNumber("AGR-2025-001");
        activeDeal.setAgreementDate(LocalDate.parse("2025-11-01"));
        activeDeal.setOpenedAt(LocalDateTime.parse("2025-11-01T10:00:00"));
        activeDeal.setType(DealType.CREDIT);
        activeDeal.setStatus(DealStatus.ACTIVE);

        activeDeal.addContractor(contractorAutoMir);
        activeDeal.addContractor(contractorNba);
        activeDeal.addContractor(contractorNvidea);

        List<Contractor> contractorList = List.of(
                contractorAutoMir,
                contractorNba,
                contractorNvidea
        );

        Mockito.when(contractorRepositoryMock.findWithFilters(
                Mockito.eq(List.of("KZ", "USA")),
                Mockito.isNull(),
                Mockito.eq(List.of("АО")),
                Mockito.eq(LocalDate.parse("2024-01-01")),
                Mockito.eq(LocalDate.parse("2027-12-31"))
        )).thenReturn(contractorList);

        String requestJson = """
                {
                  "dimensions": ["country", "industry", "org_form"],
                  "metrics": ["count", "active_deals_count"],
                  "filters": {
                    "countries": ["KZ", "USA"],
                    "orgForms": ["АО"],
                    "dateRange": {
                      "createDate": {
                        "from": "2024-01-01",
                        "to": "2027-12-31"
                      }
                    }
                  },
                  "includeTrends": true
                }
                """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/contractor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())

                // summary
                .andExpect(MockMvcResultMatchers.jsonPath("$.summary").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.summary.totalContractors").value(3))

                .andExpect(MockMvcResultMatchers.jsonPath("$.summary.activeContractors").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.summary.avgDealsPerContractor").value(1.0))

                // breakdown структура
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].dimension").value("country"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].dimension").value("industry"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[2].dimension").value("org_form"))

                // country groups
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].group.id").value("USA"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].group.name").value("США"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[1].group.id").value("KZ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[1].group.name").value("Казахстан"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].metrics.count").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[0].metrics.activeDealsCount").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[1].metrics.count").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[0].data[1].metrics.activeDealsCount").value(1))

                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].group.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].group.name").value("IT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].metrics.count").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[1].data[0].metrics.activeDealsCount").value(3))

                // org_form group
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[2].data[0].group.id").value("АО"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[2].data[0].group.name").value("Акционерное общество"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[2].data[0].metrics.count").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breakdown[2].data[0].metrics.activeDealsCount").value(3))

                // trends
                .andExpect(MockMvcResultMatchers.jsonPath("$.trends").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.trends.monthlyGrowth").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.trends.monthlyGrowth[0].period").value("2025-12"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.trends.monthlyGrowth[0].newContractors").value(3));

        Mockito.verify(contractorRepositoryMock).findWithFilters(
                Mockito.eq(List.of("KZ", "USA")),
                Mockito.isNull(),
                Mockito.eq(List.of("АО")),
                Mockito.eq(LocalDate.parse("2024-01-01")),
                Mockito.eq(LocalDate.parse("2027-12-31"))
        );
    }

    /**
     * Проверяем, что при неправильных данных, будет выведена ошибка и корректное сообщение
     */
    @Test
    void badDataRequestTest() throws Exception {
        String invalidJson = """
            {
              "dimensions": ["country"],
              "metrics": ["count"],
              "filters": {
                "countries": ["KZ"],
                "dateRange": {
                  "createDate": {
                    "from": "2021234-01-01",
                    "to": "2027-12-31"
                  }
                }
              },
              "includeTrends": true
            }
            """;

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/analytics/contractor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidJson)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "filters.dateRange.createDate.from: Поле from должно быть в формате ГГГГ-ММ-ДД"
                        )));
    }
}
