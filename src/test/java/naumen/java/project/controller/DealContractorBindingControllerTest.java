package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.DealContractorRequestDTO;
import naumen.java.project.exepction.GlobalExceptionHandler;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorBindingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

/**
 * Тесты для DealContractorBindingController
 *
 * @author Daria
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты DealContractorBindingController")
class DealContractorBindingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final DealMapper dealMapper;
    private final DealContractorBindingService dealContractorBindingService;

    public DealContractorBindingControllerTest(@Mock DealContractorBindingService dealContractorBindingService) {
        this.dealContractorBindingService = dealContractorBindingService;
        this.dealMapper = new DealMapper();

        DealContractorBindingController dealContractorBindingController
                = new DealContractorBindingController(dealContractorBindingService, dealMapper);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(dealContractorBindingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.objectMapper = new ObjectMapper();
    }

    private static final UUID NON_EXISTENT_DEAL_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final DealTestFactory dealTestFactory = new DealTestFactory();
    private final UUID dealId = dealTestFactory.getDealId();
    private final UUID contractorId = dealTestFactory.getContractorId();
    private final DealContractorRequestDTO validRequest =
            dealTestFactory.createDealContractorRequest(dealId, contractorId);
    private final DealContractorRequestDTO invalidUuidRequest =
            new DealContractorRequestDTO("invalid-uuid", contractorId.toString());
    private final Deal deal = dealTestFactory.createDeal(dealId,
            dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

    /**
     * Проверяет корректное создание связи между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - успешное создание связи")
    void addContractorToDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.addContractorToDeal(contractorId, dealId))
                .thenReturn(deal);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()));
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - успешное удаление связи")
    void removeContractorFromDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(contractorId, dealId))
                .thenReturn(deal);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(dealId.toString()));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки в запросе
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при невалидном UUID сделки")
    void addContractorWithInvalidDealUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUuidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 400 при пустом идентификаторе контрагента
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при пустом contractorId")
    void addContractorWithEmptyContractorIdTest() throws Exception {
        DealContractorRequestDTO request = new DealContractorRequestDTO(
                dealId.toString(), "");

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
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
    @DisplayName("POST /deal-contractor/save - возвращает 404 при отсутствии сделки")
    void addContractorToNonExistentDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.addContractorToDeal(contractorId, dealId))
                .thenThrow(new ResourceNotFoundException("Сделка", dealId.toString()));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));
    }

    /**
     * Проверяет возврат 400 при нарушении бизнес-правил
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при имеющейся связи")
    void addContractorWithBusinessRuleViolationTest() throws Exception {
        Mockito.when(dealContractorBindingService.addContractorToDeal(contractorId, dealId))
                .thenThrow(new IllegalStateException("Нельзя добавить контрагента с id = "
                        + contractorId + ", так как уже существует связь"));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ILLEGAL_STATE"));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки при удалении
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 400 при невалидном UUID сделки")
    void removeContractorWithInvalidDealUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUuidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));
    }

    /**
     * Проверяет возврат 404 при отсутствии связи для удаления
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 404 при отсутствии связи")
    void removeNonExistentContractorFromDealTest() throws Exception {
        String nonExistentContractorId = "NON_EXISTENT_CONTRACTOR";
        DealContractorRequestDTO request = dealTestFactory.createDealContractorRequest(dealId, NON_EXISTENT_DEAL_ID);

        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(NON_EXISTENT_DEAL_ID, dealId))
                .thenThrow(new ResourceNotFoundException("Контрагент", nonExistentContractorId));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("ENTITY_NOT_FOUND"));
    }
}