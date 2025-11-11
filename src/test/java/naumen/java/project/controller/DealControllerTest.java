package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.service.DealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
    private final TestHelperDeal testHelperDeal = new TestHelperDeal();

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("POST /deal/save - создание новой сделки")
    void createNewDealTest() throws Exception {
        DealRequest request = testHelperDeal.createDealRequest(null,
                testHelperDeal.getDescription());
        Deal createdDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealShortResponse shortResponse = testHelperDeal.createDealShortResponse(
                testHelperDeal.getDealId(), testHelperDeal.getDealStatus());

        Mockito.when(dealService.createOrUpdate(ArgumentMatchers.any(DealRequest.class)))
                .thenReturn(createdDeal);
        Mockito.when(dealMapper.toResponse(createdDeal))
                .thenReturn(shortResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(testHelperDeal.getDealStatus().name()));

        Mockito.verify(dealService).createOrUpdate(ArgumentMatchers.any(DealRequest.class));
        Mockito.verify(dealMapper).toResponse(createdDeal);
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("POST /deal/save - обновление существующей сделки")
    void updateExistingDealTest() throws Exception {
        DealRequest request = testHelperDeal.createDealRequest(testHelperDeal.getDealId(),
                "Обновленное описание");
        Deal updatedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                "Обновленное описание", DealStatus.ACTIVE);
        DealShortResponse shortResponse = testHelperDeal.createDealShortResponse(
                testHelperDeal.getDealId(), DealStatus.ACTIVE);

        Mockito.when(dealService.createOrUpdate(ArgumentMatchers.any(DealRequest.class)))
                .thenReturn(updatedDeal);
        Mockito.when(dealMapper.toResponse(updatedDeal))
                .thenReturn(shortResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(DealStatus.ACTIVE.name()));

        Mockito.verify(dealService).createOrUpdate(ArgumentMatchers.any(DealRequest.class));
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("GET /deal/{id} - получение сделки с контрагентами")
    void getByIdTest() throws Exception {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenReturn(deal);
        Mockito.when(dealMapper.tolResponse(deal))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}",
                        testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testHelperDeal.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber")
                        .value(testHelperDeal.getAgreementNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type")
                        .value(testHelperDeal.getDealType().getDisplayName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(testHelperDeal.getDealStatus().getDisplayName()));

        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(dealMapper).tolResponse(deal);
    }

    /**
     * Проверяет корректное удаление сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - удаление сделки")
    void deleteDealTest() throws Exception {
        Mockito.doNothing().when(dealService).delete(testHelperDeal.getDealId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(dealService).delete(testHelperDeal.getDealId());
        Mockito.verifyNoInteractions(dealMapper);
    }

    /**
     * Проверяет корректное получение списка всех сделок
     */
    @Test
    @DisplayName("GET /deal/all - получение всех сделок")
    void findAllTest() throws Exception {
        Deal deal1 = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                "Сделка 1", testHelperDeal.getDealStatus());
        Deal deal2 = testHelperDeal.createDeal(UUID.randomUUID(),
                "Сделка 2", DealStatus.ACTIVE);
        List<Deal> deals = List.of(deal1, deal2);
        DealResponse response1 = testHelperDeal.createDealResponse(testHelperDeal.getDealId(),
                "Сделка 1", testHelperDeal.getDealStatus());
        DealResponse response2 = testHelperDeal.createDealResponse(UUID.randomUUID(),
                "Сделка 2", DealStatus.ACTIVE);
        List<DealResponse> responses = List.of(response1, response2);

        Mockito.when(dealService.findAllWithContractors())
                .thenReturn(deals);
        Mockito.when(dealMapper.toListResponse(deals))
                .thenReturn(responses);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description")
                        .value("Сделка 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description")
                        .value("Сделка 2"));

        Mockito.verify(dealService).findAllWithContractors();
        Mockito.verify(dealMapper).toListResponse(deals);
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - изменение статуса сделки")
    void changeStatusTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), newStatus);
        DealResponse response = testHelperDeal.createDealResponse(
                testHelperDeal.getDealId(), testHelperDeal.getDescription(), newStatus);

        Mockito.when(dealService.changeStatus(testHelperDeal.getDealId(), newStatus))
                .thenReturn(updatedDeal);
        Mockito.when(dealMapper.tolResponse(updatedDeal))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        testHelperDeal.getDealId().toString(), newStatus.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(newStatus.getDisplayName()));

        Mockito.verify(dealService).changeStatus(testHelperDeal.getDealId(), newStatus);
        Mockito.verify(dealMapper).tolResponse(updatedDeal);
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

        Mockito.verify(dealService, Mockito.never()).findByIdWithContractors(ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии сделки
     */
    @Test
    @DisplayName("GET /deal/{id} - возвращает 404 при отсутствии сделки")
    void getByIdWithNonExistentDealTest() throws Exception {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.when(dealService.findByIdWithContractors(nonExistentId))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}", nonExistentId.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));

        Mockito.verify(dealService).findByIdWithContractors(nonExistentId);
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
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

        Mockito.verify(dealService, Mockito.never()).delete(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидном статусе сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - возвращает 400 при невалидном статусе")
    void changeStatusWithInvalidStatusTest() throws Exception {
        String validUuid = testHelperDeal.getDealId().toString();
        String invalidStatus = "INVALID_STATUS";

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        validUuid, invalidStatus))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("INVALID_INPUT"));

        Mockito.verify(dealService, Mockito.never()).changeStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидном UUID при изменении статуса
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - возвращает 400 при невалидном UUID")
    void changeStatusWithInvalidUuidTest() throws Exception {
        String invalidUuid = "invalid-uuid-123";
        String validStatus = DealStatus.ACTIVE.name();

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        invalidUuid, validStatus))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealService, Mockito.never()).changeStatus(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидных данных в теле запроса
     */
    @Test
    @DisplayName("POST /deal/save - возвращает 400 при невалидных данных в DTO")
    void createDealWithInvalidRequestBodyTest() throws Exception {
        DealRequest invalidRequest = new DealRequest(
                "invalid-uuid",
                "",
                "",
                "invalid-date",
                "invalid-datetime",
                "invalid-datetime",
                "INVALID_TYPE",
                "INVALID_STATUS"
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

        Mockito.verify(dealService, Mockito.never()).createOrUpdate(ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).toResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при удалении несуществующей сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - возвращает 404 при несуществующей сделке")
    void deleteNonExistentDealTest() throws Exception {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.doThrow(new EntityNotFoundException("Deal not found"))
                .when(dealService).delete(nonExistentId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        nonExistentId.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));

        Mockito.verify(dealService).delete(nonExistentId);
        Mockito.verifyNoInteractions(dealMapper);
    }

    /**
     * Проверяет возврат 400 при удалении сделки с привязанными контрагентами
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - возвращает 400 при наличии контрагентов")
    void deleteDealWithContractorsTest() throws Exception {
        Mockito.doThrow(new IllegalStateException("Deal use in contractor"))
                .when(dealService).delete(testHelperDeal.getDealId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));

        Mockito.verify(dealService).delete(testHelperDeal.getDealId());
        Mockito.verifyNoInteractions(dealMapper);
    }
}