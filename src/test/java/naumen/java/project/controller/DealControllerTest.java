package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.deal.DealRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.factory.DealTestFactory;
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
    private final DealTestFactory dealTestFactory = new DealTestFactory();

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("POST /deal/save - создание новой сделки")
    void createNewDealTest() throws Exception {
        DealRequestDTO request = dealTestFactory.createDealRequest(null,
                dealTestFactory.getDescription());
        Deal createdDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        DealShortResponseDTO shortResponse = dealTestFactory.createDealShortResponse(
                dealTestFactory.getDealId(), dealTestFactory.getDealStatus());

        Mockito.when(dealService.createOrUpdate(ArgumentMatchers.any(Deal.class)))
                .thenReturn(createdDeal);
        Mockito.when(dealMapper.toShortResponse(createdDeal))
                .thenReturn(shortResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(dealTestFactory.getDealStatus().name()));

        Mockito.verify(dealService).createOrUpdate(ArgumentMatchers.any(Deal.class));
        Mockito.verify(dealMapper).toShortResponse(createdDeal);
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("POST /deal/save - обновление существующей сделки")
    void updateExistingDealTest() throws Exception {
        DealRequestDTO request = dealTestFactory.createDealRequest(dealTestFactory.getDealId(),
                "Обновленное описание");
        Deal updatedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                "Обновленное описание", DealStatus.ACTIVE);
        DealShortResponseDTO shortResponse = dealTestFactory.createDealShortResponse(
                dealTestFactory.getDealId(), DealStatus.ACTIVE);

        Mockito.when(dealService.createOrUpdate(ArgumentMatchers.any(Deal.class)))
                .thenReturn(updatedDeal);
        Mockito.when(dealMapper.toShortResponse(updatedDeal))
                .thenReturn(shortResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(DealStatus.ACTIVE.name()));

        Mockito.verify(dealService).createOrUpdate(ArgumentMatchers.any(Deal.class));
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("GET /deal/{id} - получение сделки с контрагентами")
    void getByIdTest() throws Exception {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        DealResponseDTO response = dealTestFactory.createDealResponse(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenReturn(deal);
        Mockito.when(dealMapper.toDetailResponse(deal))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/{id}",
                        dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(dealTestFactory.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber")
                        .value(dealTestFactory.getAgreementNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type")
                        .value(dealTestFactory.getDealType().getDisplayName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(dealTestFactory.getDealStatus().getDisplayName()));

        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(dealMapper).toDetailResponse(deal);
    }

    /**
     * Проверяет корректное удаление сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - удаление сделки")
    void deleteDealTest() throws Exception {
        Mockito.doNothing().when(dealService).delete(dealTestFactory.getDealId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));

        Mockito.verify(dealService).delete(dealTestFactory.getDealId());
        Mockito.verifyNoInteractions(dealMapper);
    }

    /**
     * Проверяет корректное получение списка всех сделок
     */
    @Test
    @DisplayName("GET /deal/all - получение всех сделок")
    void findAllTest() throws Exception {
        Deal deal1 = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                "Сделка 1", dealTestFactory.getDealStatus());
        Deal deal2 = dealTestFactory.createDeal(UUID.randomUUID(),
                "Сделка 2", DealStatus.ACTIVE);
        List<Deal> deals = List.of(deal1, deal2);
        DealResponseDTO response1 = dealTestFactory.createDealResponse(dealTestFactory.getDealId(),
                "Сделка 1", dealTestFactory.getDealStatus());
        DealResponseDTO response2 = dealTestFactory.createDealResponse(UUID.randomUUID(),
                "Сделка 2", DealStatus.ACTIVE);
        List<DealResponseDTO> responses = List.of(response1, response2);

        Mockito.when(dealService.findAllWithContractors())
                .thenReturn(deals);
        Mockito.when(dealMapper.toListResponse(deals))
                .thenReturn(responses);

        mockMvc.perform(MockMvcRequestBuilders.get("/deal/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()")
                        .value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(dealTestFactory.getDealId().toString()))
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
        Deal updatedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), newStatus);
        DealResponseDTO response = dealTestFactory.createDealResponse(
                dealTestFactory.getDealId(), dealTestFactory.getDescription(), newStatus);

        Mockito.when(dealService.changeStatus(dealTestFactory.getDealId(), newStatus))
                .thenReturn(updatedDeal);
        Mockito.when(dealMapper.toDetailResponse(updatedDeal))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.patch("/deal/change/status/{id}/{status}",
                        dealTestFactory.getDealId().toString(), newStatus.name()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(newStatus.getDisplayName()));

        Mockito.verify(dealService).changeStatus(dealTestFactory.getDealId(), newStatus);
        Mockito.verify(dealMapper).toDetailResponse(updatedDeal);
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
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
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
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
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
        String validUuid = dealTestFactory.getDealId().toString();
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
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
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
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидных данных в теле запроса
     */
    @Test
    @DisplayName("POST /deal/save - возвращает 400 при невалидных данных в DTO")
    void createDealWithInvalidRequestBodyTest() throws Exception {
        DealRequestDTO invalidRequest = new DealRequestDTO(
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
        Mockito.verify(dealMapper, Mockito.never()).toShortResponse(ArgumentMatchers.any());
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
                .when(dealService).delete(dealTestFactory.getDealId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/deal/delete/{id}",
                        dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));

        Mockito.verify(dealService).delete(dealTestFactory.getDealId());
        Mockito.verifyNoInteractions(dealMapper);
    }
}