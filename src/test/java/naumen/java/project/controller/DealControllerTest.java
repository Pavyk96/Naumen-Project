package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.deal.DealRequestDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.service.DealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

/**
 * Тесты для DealController
 *
 * @author Daria
 */
@WebMvcTest(DealController.class)
@Import(DealMapper.class)
@DisplayName("Тесты DealController")
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DealMapper dealMapper;
    @MockitoBean
    private DealService dealService;

    private static final UUID NON_EXISTENT_DEAL_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final DealTestFactory dealTestFactory = new DealTestFactory();
    private final UUID dealId = dealTestFactory.getDealId();
    private final String description = dealTestFactory.getDescription();

    private final DealStatus dealStatus = dealTestFactory.getDealStatus();

    private final Deal deal = dealTestFactory.createDeal(dealId,
            description, dealStatus);

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("POST /deal/save - создание новой сделки")
    void createNewDealTest() throws Exception {
        DealRequestDTO request = dealTestFactory.createDealRequest(null, description);

        Mockito.when(dealService.save(Mockito.any(Deal.class))).thenReturn(deal);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(dealStatus.name()));
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("POST /deal/save - обновление существующей сделки")
    void updateExistingDealTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = dealTestFactory.createDeal(dealId, "Обновленное описание", newStatus);
        DealRequestDTO dealRequest = dealTestFactory.createDealRequest(dealId, description);

        Mockito.when(dealService.findById(dealId)).thenReturn(deal);
        Mockito.when(dealService.save(Mockito.any(Deal.class))).thenReturn(updatedDeal);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dealRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(newStatus.name()));
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("GET /deal/{id} - получение сделки с контрагентами")
    void getByIdTest() throws Exception {
        Mockito.when(dealService.findByIdWithContractors(dealId)).thenReturn(deal);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", dealId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber").value(dealTestFactory.getAgreementNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(dealTestFactory.getDealType().getDisplayName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(dealStatus.getDisplayName()));
    }

    /**
     * Проверяет корректное удаление сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - удаление сделки")
    void deleteDealTest() throws Exception {
        Mockito.doNothing().when(dealService).delete(dealId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}", dealId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    /**
     * Проверяет корректное получение списка всех сделок
     */
    @Test
    @DisplayName("GET /deal/all - получение всех сделок")
    void findAllTest() throws Exception {
        UUID dealId2 = UUID.randomUUID();
        Deal deal2 = dealTestFactory.createDeal(dealId2, "Сделка 2", DealStatus.ACTIVE);
        List<Deal> deals = List.of(deal, deal2);

        Mockito.when(dealService.findAllWithContractors()).thenReturn(deals);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(dealId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(description))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Сделка 2"));
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - изменение статуса сделки")
    void changeStatusTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = dealTestFactory.createDeal(dealId, description, newStatus);

        Mockito.when(dealService.changeStatus(dealId, newStatus)).thenReturn(updatedDeal);

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        dealId.toString(), newStatus.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(newStatus.getDisplayName()));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID в path variable
     */
    @Test
    @DisplayName("GET /deal/{id} - возвращает 400 при невалидном UUID")
    void getByIdWithInvalidUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}",
                        "invalid-uuid-format"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 404 при отсутствии сделки
     */
    @Test
    @DisplayName("GET /deal/{id} - возвращает 404 при отсутствии сделки")
    void getByIdWithNonExistentDealTest() throws Exception {
        Mockito.when(dealService.findByIdWithContractors(NON_EXISTENT_DEAL_ID))
                .thenThrow(new ResourceNotFoundException("Сделка", NON_EXISTENT_DEAL_ID.toString()));

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", NON_EXISTENT_DEAL_ID))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID при удалении сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - возвращает 400 при невалидном UUID")
    void deleteDealWithInvalidUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        "not-a-uuid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("INVALID_INPUT"));
    }

    /**
     * Проверяет возврат 400 при невалидном статусе сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - возвращает 400 при невалидном статусе")
    void changeStatusWithInvalidStatusTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        dealId.toString(), "INVALID_STATUS"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID при изменении статуса
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - возвращает 400 при невалидном UUID")
    void changeStatusWithInvalidUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        "invalid-uuid-123", DealStatus.ACTIVE.name()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 400 при невалидных данных в теле запроса
     */
    @Test
    @DisplayName("POST /deal/save - возвращает 400 при невалидных данных в DTO")
    void createDealWithInvalidRequestBodyTest() throws Exception {
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "invalid-uuid", "", "", "invalid-date", "invalid-datetime",
                "invalid-datetime", "INVALID_TYPE", "INVALID_STATUS"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 404 при удалении несуществующей сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - возвращает 404 при несуществующей сделке")
    void deleteNonExistentDealTest() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Сделка", NON_EXISTENT_DEAL_ID.toString()))
                .when(dealService).delete(NON_EXISTENT_DEAL_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        NON_EXISTENT_DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));
    }

    /**
     * Проверяет возврат 400 при удалении сделки с привязанными контрагентами
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - возвращает 400 при наличии контрагентов")
    void deleteDealWithContractorsTest() throws Exception {
        Mockito.doThrow(new IllegalStateException("Нельзя удалить сделку с id = "
                        + dealId + ", так как к ней привязаны контрагенты"))
                .when(dealService).delete(dealId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}", dealId.toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));
    }
}