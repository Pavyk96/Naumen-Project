package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.deal.DealRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.service.DealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
@DisplayName("Тесты DealController")
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private DealService dealService;
    @MockitoBean
    private DealMapper dealMapper;

    private static final DealTestFactory DEAL_TEST_FACTORY = new DealTestFactory();
    private static final UUID DEAL_ID = DEAL_TEST_FACTORY.getDealId();
    private static final UUID NON_EXISTENT_DEAL_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final String DESCRIPTION = DEAL_TEST_FACTORY.getDescription();
    private static final DealStatus DEAL_STATUS = DEAL_TEST_FACTORY.getDealStatus();
    private static final DealType DEAL_TYPE = DEAL_TEST_FACTORY.getDealType();
    private static final String AGREEMENT_NUMBER = DEAL_TEST_FACTORY.getAgreementNumber();

    private static final Deal DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID,
            DESCRIPTION, DEAL_STATUS);
    private static final DealRequestDTO DEAL_REQUEST = DEAL_TEST_FACTORY
            .createDealRequest(DEAL_ID, DESCRIPTION);
    private static final DealShortResponseDTO DEAL_SHORT_RESPONSE = DEAL_TEST_FACTORY
            .createDealShortResponse(DEAL_ID, DEAL_STATUS);
    private static final DealResponseDTO DEAL_RESPONSE = DEAL_TEST_FACTORY
            .createDealResponse(DEAL_ID, DESCRIPTION, DEAL_STATUS);

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("POST /deal/save - создание новой сделки")
    void createNewDealTest() throws Exception {
        DealRequestDTO request = DEAL_TEST_FACTORY.createDealRequest(null, DESCRIPTION);

        Mockito.when(dealService.createOrUpdate(Mockito.any(Deal.class))).thenReturn(DEAL);
        Mockito.when(dealMapper.toShortResponse(DEAL)).thenReturn(DEAL_SHORT_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(DEAL_STATUS.name()));
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("POST /deal/save - обновление существующей сделки")
    void updateExistingDealTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = DEAL_TEST_FACTORY.createDeal(DEAL_ID, "Обновленное описание", newStatus);
        DealShortResponseDTO shortResponse = DEAL_TEST_FACTORY.createDealShortResponse(DEAL_ID, newStatus);

        Mockito.when(dealService.createOrUpdate(Mockito.any(Deal.class))).thenReturn(updatedDeal);
        Mockito.when(dealMapper.toShortResponse(updatedDeal)).thenReturn(shortResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(DEAL_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(newStatus.name()));
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("GET /deal/{id} - получение сделки с контрагентами")
    void getByIdTest() throws Exception {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(DEAL);
        Mockito.when(dealMapper.toDetailResponse(DEAL)).thenReturn(DEAL_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(DESCRIPTION))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber").value(AGREEMENT_NUMBER))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(DEAL_TYPE.getDisplayName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(DEAL_STATUS.getDisplayName()));
    }

    /**
     * Проверяет корректное удаление сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - удаление сделки")
    void deleteDealTest() throws Exception {
        Mockito.doNothing().when(dealService).delete(DEAL_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}", DEAL_ID.toString()))
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
        Deal deal2 = DEAL_TEST_FACTORY.createDeal(dealId2, "Сделка 2", DealStatus.ACTIVE);
        List<Deal> deals = List.of(DEAL, deal2);

        DealResponseDTO response2 = DEAL_TEST_FACTORY.createDealResponse(dealId2, "Сделка 2", DealStatus.ACTIVE);
        List<DealResponseDTO> responses = List.of(DEAL_RESPONSE, response2);

        Mockito.when(dealService.findAllWithContractors()).thenReturn(deals);
        Mockito.when(dealMapper.toListResponse(deals)).thenReturn(responses);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(DESCRIPTION))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Сделка 2"));
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - изменение статуса сделки")
    void changeStatusTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = DEAL_TEST_FACTORY.createDeal(DEAL_ID, DESCRIPTION, newStatus);
        DealResponseDTO response = DEAL_TEST_FACTORY.createDealResponse(DEAL_ID, DESCRIPTION, newStatus);

        Mockito.when(dealService.changeStatus(DEAL_ID, newStatus)).thenReturn(updatedDeal);
        Mockito.when(dealMapper.toDetailResponse(updatedDeal)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        DEAL_ID.toString(), newStatus.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()))
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

        Mockito.verify(dealService, Mockito.never()).findByIdWithContractors(Mockito.any());
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

        Mockito.verify(dealService, Mockito.never()).delete(Mockito.any());
    }

    /**
     * Проверяет возврат 400 при невалидном статусе сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - возвращает 400 при невалидном статусе")
    void changeStatusWithInvalidStatusTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        DEAL_ID.toString(), "INVALID_STATUS"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealService, Mockito.never()).changeStatus(Mockito.any(), Mockito.any());
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

        Mockito.verify(dealService, Mockito.never()).changeStatus(Mockito.any(), Mockito.any());
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
                        .content(om.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealService, Mockito.never()).createOrUpdate(Mockito.any());
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
                        + DEAL_ID + ", так как к ней привязаны контрагенты"))
                .when(dealService).delete(DEAL_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}", DEAL_ID.toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));
    }
}