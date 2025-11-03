package naumen.java.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.service.DealContractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal dealWithContractor = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        when(dealContractorService.addContractorToDeal(any(DealContractorRequest.class))).thenReturn(dealWithContractor);
        when(dealMapper.tolResponse(dealWithContractor)).thenReturn(response);

        mockMvc.perform(post("/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.description").value(testHelperDeal.getDescription()))
                .andExpect(jsonPath("$.agreementNumber").value(testHelperDeal.getAgreementNumber()))
                .andExpect(jsonPath("$.type").value(testHelperDeal.getDealType().getDisplayName()))
                .andExpect(jsonPath("$.status").value(testHelperDeal.getDealStatus().getDisplayName()));

        verify(dealContractorService).addContractorToDeal(any(DealContractorRequest.class));
        verify(dealMapper).tolResponse(dealWithContractor);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("POST /deal-contractor/delete - успешное удаление связи")
    void removeContractorFromDealTest() throws Exception {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal dealWithoutContractor = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        DealResponse response = testHelperDeal.createDealResponse(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        when(dealContractorService.deleteContractorFromDeal(any(DealContractorRequest.class))).thenReturn(dealWithoutContractor);
        when(dealMapper.tolResponse(dealWithoutContractor)).thenReturn(response);

        mockMvc.perform(post("/deal-contractor/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHelperDeal.getDealId().toString()))
                .andExpect(jsonPath("$.description").value(testHelperDeal.getDescription()))
                .andExpect(jsonPath("$.agreementNumber").value(testHelperDeal.getAgreementNumber()));

        verify(dealContractorService).deleteContractorFromDeal(any(DealContractorRequest.class));
        verify(dealMapper).tolResponse(dealWithoutContractor);
    }
}
