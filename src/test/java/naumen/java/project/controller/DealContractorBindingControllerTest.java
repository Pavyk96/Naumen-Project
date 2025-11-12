package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.DealContractorRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorBindingService;
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

import java.util.UUID;

/**
 * Тесты для DealContractorBindingController
 *
 * @author Daria
 */
@WebMvcTest(DealContractorBindingController.class)
@DisplayName("Тесты DealContractorBindingController")
class DealContractorBindingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;
    @MockitoBean
    private DealContractorBindingService dealContractorBindingService;
    @MockitoBean
    private DealMapper dealMapper;
    private final DealTestFactory dealTestFactory = new DealTestFactory();

    /**
     * Проверяет корректное создание связи между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - успешное создание связи")
    void addContractorToDealTest() throws Exception {
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(
                dealTestFactory.getDealId(), dealTestFactory.getContractorId());
        Deal dealWithContractor = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        DealResponseDTO response = dealTestFactory.createDealResponse(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(dealContractorBindingService.addContractorToDeal(
                        request.contractorId(), UUID.fromString(request.dealId())))
                .thenReturn(dealWithContractor);
        Mockito.when(dealMapper.toDetailResponse(dealWithContractor))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
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

        Mockito.verify(dealContractorBindingService).addContractorToDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper).toDetailResponse(dealWithContractor);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - успешное удаление связи")
    void removeContractorFromDealTest() throws Exception {
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(
                dealTestFactory.getDealId(), dealTestFactory.getContractorId());
        Deal dealWithoutContractor = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        DealResponseDTO response = dealTestFactory.createDealResponse(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(
                        request.contractorId(), UUID.fromString(request.dealId())))
                .thenReturn(dealWithoutContractor);
        Mockito.when(dealMapper.toDetailResponse(dealWithoutContractor))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id")
                        .value(dealTestFactory.getDealId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value(dealTestFactory.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.agreementNumber")
                        .value(dealTestFactory.getAgreementNumber()));

        Mockito.verify(dealContractorBindingService).deleteContractorFromDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper).toDetailResponse(dealWithoutContractor);
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки в запросе
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при невалидном UUID сделки")
    void addContractorWithInvalidDealUuidTest() throws Exception {
        DealContractorRequestDTO request = new DealContractorRequestDTO("invalid-uuid",
                dealTestFactory.getContractorId());

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

        Mockito.verify(dealContractorBindingService, Mockito.never()).addContractorToDeal(
                ArgumentMatchers.any(String.class), ArgumentMatchers.any(UUID.class));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при пустом идентификаторе контрагента
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при пустом contractorId")
    void addContractorWithEmptyContractorIdTest() throws Exception {
        DealContractorRequestDTO request = new DealContractorRequestDTO(
                dealTestFactory.getDealId().toString(), "");

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

        Mockito.verify(dealContractorBindingService, Mockito.never()).addContractorToDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 404 при отсутствии сделки")
    void addContractorToNonExistentDealTest() throws Exception {
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(
                dealTestFactory.getDealId(), dealTestFactory.getContractorId());

        Mockito.when(dealContractorBindingService.addContractorToDeal(
                        request.contractorId(), UUID.fromString(request.dealId())))
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

        Mockito.verify(dealContractorBindingService).addContractorToDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при нарушении бизнес-правил:
     * уже существует связь между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при имеющей связи")
    void addContractorWithBusinessRuleViolationTest() throws Exception {
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(
                dealTestFactory.getDealId(), dealTestFactory.getContractorId());

        Mockito.when(dealContractorBindingService.addContractorToDeal(
                        request.contractorId(), UUID.fromString(request.dealId())))
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

        Mockito.verify(dealContractorBindingService).addContractorToDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки при удалении
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 400 при невалидном UUID сделки")
    void removeContractorWithInvalidDealUuidTest() throws Exception {
        DealContractorRequestDTO request = new DealContractorRequestDTO("invalid-uuid",
                dealTestFactory.getContractorId());

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

        Mockito.verify(dealContractorBindingService, Mockito.never()).deleteContractorFromDeal(
                ArgumentMatchers.any(String.class), ArgumentMatchers.any(UUID.class));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии связи для удаления
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 404 при отсутствии связи")
    void removeNonExistentContractorFromDealTest() throws Exception {
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(
                dealTestFactory.getDealId(), "NON_EXISTENT_CONTRACTOR");

        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(
                        request.contractorId(), UUID.fromString(request.dealId())))
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

        Mockito.verify(dealContractorBindingService).deleteContractorFromDeal(
                request.contractorId(), UUID.fromString(request.dealId()));
        Mockito.verify(dealMapper, Mockito.never()).toDetailResponse(ArgumentMatchers.any());
    }
}
