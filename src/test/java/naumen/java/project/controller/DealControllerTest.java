package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
        DealRequest request = testHelperDeal.createDealRequest(null, testHelperDeal.getDescription());
        Deal createdDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealShortResponse shortResponse = testHelperDeal.createDealShortResponse(testHelperDeal.getDealId(), testHelperDeal.getDealStatus());

        when(dealService.createOrUpdate(any(DealRequest.class))).thenReturn(createdDeal);
        when(dealMapper.toResponse(createdDeal)).thenReturn(shortResponse);

        mockMvc.perform(post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.status").value(testHelperDeal.getDealStatus().name()));

        verify(dealService).createOrUpdate(any(DealRequest.class));
        verify(dealMapper).toResponse(createdDeal);
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("POST /deal/save - обновление существующей сделки")
    void updateExistingDealTest() throws Exception {
        DealRequest request = testHelperDeal.createDealRequest(testHelperDeal.getDealId(), "Обновленное описание");
        Deal updatedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Обновленное описание", DealStatus.ACTIVE);
        DealShortResponse shortResponse = testHelperDeal.createDealShortResponse(testHelperDeal.getDealId(), DealStatus.ACTIVE);

        when(dealService.createOrUpdate(any(DealRequest.class))).thenReturn(updatedDeal);
        when(dealMapper.toResponse(updatedDeal)).thenReturn(shortResponse);

        mockMvc.perform(post("/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.status").value(DealStatus.ACTIVE.name()));

        verify(dealService).createOrUpdate(any(DealRequest.class));
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("GET /deal/{id} - получение сделки с контрагентами")
    void getByIdTest() throws Exception {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        when(dealService.findByIdWithContractors(testHelperDeal.getDealId())).thenReturn(deal);
        when(dealMapper.tolResponse(deal)).thenReturn(response);

        mockMvc.perform(get("/deal/{id}", testHelperDeal.getDealId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.description").value(testHelperDeal.getDescription()))
                .andExpect(jsonPath("$.agreementNumber").value(testHelperDeal.getAgreementNumber()))
                .andExpect(jsonPath("$.type").value(testHelperDeal.getDealType().getDisplayName()))
                .andExpect(jsonPath("$.status").value(testHelperDeal.getDealStatus().getDisplayName()));

        verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        verify(dealMapper).tolResponse(deal);
    }

    /**
     * Проверяет корректное удаление сделки
     */
    @Test
    @DisplayName("DELETE /deal/delete/{id} - удаление сделки")
    void deleteDealTest() throws Exception {
        doNothing().when(dealService).delete(testHelperDeal.getDealId());

        mockMvc.perform(delete("/deal/delete/{id}", testHelperDeal.getDealId()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(dealService).delete(testHelperDeal.getDealId());
        verifyNoInteractions(dealMapper);
    }

    /**
     * Проверяет корректное получение списка всех сделок
     */
    @Test
    @DisplayName("GET /deal/all - получение всех сделок")
    void findAllTest() throws Exception {
        Deal deal1 = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Сделка 1", testHelperDeal.getDealStatus());
        Deal deal2 = testHelperDeal.createDeal(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE);
        List<Deal> deals = List.of(deal1, deal2);
        DealResponse response1 = testHelperDeal.createDealResponse(testHelperDeal.getDealId(), "Сделка 1", testHelperDeal.getDealStatus());
        DealResponse response2 = testHelperDeal.createDealResponse(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE);
        List<DealResponse> responses = List.of(response1, response2);

        when(dealService.findAllWithContractors()).thenReturn(deals);
        when(dealMapper.toListResponse(deals)).thenReturn(responses);

        mockMvc.perform(get("/deal/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$[0].description").value("Сделка 1"))
                .andExpect(jsonPath("$[1].description").value("Сделка 2"));

        verify(dealService).findAllWithContractors();
        verify(dealMapper).toListResponse(deals);
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("PATCH /deal/change/status/{id}/{status} - изменение статуса сделки")
    void changeStatusTest() throws Exception {
        DealStatus newStatus = DealStatus.ACTIVE;
        Deal updatedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), newStatus);
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(), testHelperDeal.getDescription(), newStatus);

        when(dealService.changeStatus(testHelperDeal.getDealId(), newStatus)).thenReturn(updatedDeal);
        when(dealMapper.tolResponse(updatedDeal)).thenReturn(response);

        mockMvc.perform(patch("/deal/change/status/{id}/{status}", testHelperDeal.getDealId(), newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.status").value(newStatus.getDisplayName()));

        verify(dealService).changeStatus(testHelperDeal.getDealId(), newStatus);
        verify(dealMapper).tolResponse(updatedDeal);
    }
}