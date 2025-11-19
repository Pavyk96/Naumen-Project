package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.DealContractorRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorBindingService;
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

    private static final DealTestFactory DEAL_TEST_FACTORY = new DealTestFactory();
    private static final UUID DEAL_ID = DEAL_TEST_FACTORY.getDealId();
    private static final String CONTRACTOR_ID = DEAL_TEST_FACTORY.getContractorId();
    private static final DealContractorRequestDTO VALID_REQUEST =
            DEAL_TEST_FACTORY.createDealContractorRequest(DEAL_ID, CONTRACTOR_ID);
    private static final DealContractorRequestDTO INVALID_UUID_REQUEST =
            new DealContractorRequestDTO("invalid-uuid", CONTRACTOR_ID);
    private static final Deal DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID,
            DEAL_TEST_FACTORY.getDescription(), DEAL_TEST_FACTORY.getDealStatus());
    private static final DealResponseDTO DEAL_RESPONSE = DEAL_TEST_FACTORY.createDealResponse(DEAL_ID,
            DEAL_TEST_FACTORY.getDescription(), DEAL_TEST_FACTORY.getDealStatus());

    /**
     * Проверяет корректное создание связи между сделкой и контрагентом
     */
    @Test
    @DisplayName("POST /deal-contractor/save - успешное создание связи")
    void addContractorToDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID))
                .thenReturn(DEAL);
        Mockito.when(dealMapper.toDetailResponse(DEAL)).thenReturn(DEAL_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(VALID_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()));
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - успешное удаление связи")
    void removeContractorFromDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(CONTRACTOR_ID, DEAL_ID))
                .thenReturn(DEAL);
        Mockito.when(dealMapper.toDetailResponse(DEAL)).thenReturn(DEAL_RESPONSE);

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(VALID_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(DEAL_ID.toString()));
    }

    /**
     * Проверяет возврат 400 при невалидном UUID сделки в запросе
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при невалидном UUID сделки")
    void addContractorWithInvalidDealUuidTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(INVALID_UUID_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealContractorBindingService, Mockito.never())
                .addContractorToDeal(Mockito.any(), Mockito.any());
    }

    /**
     * Проверяет возврат 400 при пустом идентификаторе контрагента
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 400 при пустом contractorId")
    void addContractorWithEmptyContractorIdTest() throws Exception {
        DealContractorRequestDTO request = new DealContractorRequestDTO(
                DEAL_ID.toString(), "");

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
    }

    /**
     * Проверяет возврат 404 при отсутствии сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/save - возвращает 404 при отсутствии сделки")
    void addContractorToNonExistentDealTest() throws Exception {
        Mockito.when(dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID))
                .thenThrow(new ResourceNotFoundException("Сделка", DEAL_ID.toString()));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(VALID_REQUEST)))
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
        Mockito.when(dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID))
                .thenThrow(new IllegalStateException("Нельзя добавить контрагента с id = "
                        + CONTRACTOR_ID + ", так как уже существует связь"));

        mockMvc.perform(MockMvcRequestBuilders.post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(VALID_REQUEST)))
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
                        .content(om.writeValueAsString(INVALID_UUID_REQUEST)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value("VALIDATION_FAILED"));

        Mockito.verify(dealContractorBindingService, Mockito.never())
                .deleteContractorFromDeal(Mockito.any(), Mockito.any());
    }

    /**
     * Проверяет возврат 404 при отсутствии связи для удаления
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - возвращает 404 при отсутствии связи")
    void removeNonExistentContractorFromDealTest() throws Exception {
        String nonExistentContractorId = "NON_EXISTENT_CONTRACTOR";
        DealContractorRequestDTO request = DEAL_TEST_FACTORY.createDealContractorRequest(DEAL_ID, nonExistentContractorId);

        Mockito.when(dealContractorBindingService.deleteContractorFromDeal(nonExistentContractorId, DEAL_ID))
                .thenThrow(new ResourceNotFoundException("Контрагент", nonExistentContractorId));

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
    }
}