package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorService;
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

/**
 * Тесты для DealContractorController
 *
 * @author Daria
 */
@WebMvcTest(DealContractorController.class)
@DisplayName("Тесты DealContractorController")
class DealContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private DealContractorService dealContractorService;
    @MockitoBean
    private DealMapper dealMapper;
    private final TestHelperDeal testHelperDeal = new TestHelperDeal();

    /**
     * Проверяет корректное создание связи между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - успешное создание связи")
    void addContractorToDealTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal dealWithContractor = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        Mockito.when(dealContractorService.addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class)))
                .thenReturn(dealWithContractor);
        Mockito.when(dealMapper.tolResponse(dealWithContractor))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
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

        Mockito.verify(dealContractorService).addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class));
        Mockito.verify(dealMapper).tolResponse(dealWithContractor);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - успешное удаление связи")
    void removeContractorFromDealTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal dealWithoutContractor = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(),
                testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        Mockito.when(dealContractorService.deleteContractorFromDeal(
                ArgumentMatchers.any(DealContractorRequest.class)))
                .thenReturn(dealWithoutContractor);
        Mockito.when(dealMapper.tolResponse(dealWithoutContractor))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(testHelperDeal.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(testHelperDeal.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber")
                        .value(testHelperDeal.getAgreementNumber()));

        Mockito.verify(dealContractorService).deleteContractorFromDeal(
                ArgumentMatchers.any(DealContractorRequest.class));
        Mockito.verify(dealMapper).tolResponse(dealWithoutContractor);
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки в запросе
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при невалидном UUID сделки")
    void addContractorWithInvalidDealUuidTest() throws Exception {
        DealContractorRequest request = new DealContractorRequest("invalid-uuid",
                testHelperDeal.getContractorId());

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealContractorService, Mockito.never()).addContractorToDeal(
                ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при пустом идентификаторе контрагента
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при пустом contractorId")
    void addContractorWithEmptyContractorIdTest() throws Exception {
        DealContractorRequest request = new DealContractorRequest(
                testHelperDeal.getDealId().toString(), "");

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealContractorService, Mockito.never()).addContractorToDeal(
                ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 404 при отсутствии сделки")
    void addContractorToNonExistentDealTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());

        Mockito.when(dealContractorService.addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class)))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));

        Mockito.verify(dealContractorService).addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class));
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при нарушении бизнес-правил:
     * уже существует связь между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при имеющей связи")
    void addContractorWithBusinessRuleViolationTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());

        Mockito.when(dealContractorService.addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class)))
                .thenThrow(new IllegalStateException("Contractor already added to deal"));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));

        Mockito.verify(dealContractorService).addContractorToDeal(
                ArgumentMatchers.any(DealContractorRequest.class));
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки при удалении
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 400 при невалидном UUID сделки")
    void removeContractorWithInvalidDealUuidTest() throws Exception {
        DealContractorRequest request = new DealContractorRequest("invalid-uuid",
                testHelperDeal.getContractorId());

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealContractorService, Mockito.never()).deleteContractorFromDeal(
                ArgumentMatchers.any());
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии связи для удаления
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 404 при отсутствии связи")
    void removeNonExistentContractorFromDealTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), "NON_EXISTENT_CONTRACTOR");

        Mockito.when(dealContractorService.deleteContractorFromDeal(
                ArgumentMatchers.any(DealContractorRequest.class)))
                .thenThrow(new EntityNotFoundException("Contractor not found in deal"));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));

        Mockito.verify(dealContractorService).deleteContractorFromDeal(
                ArgumentMatchers.any(DealContractorRequest.class));
        Mockito.verify(dealMapper, Mockito.never()).tolResponse(ArgumentMatchers.any());
    }
}
