package naumen.java.project.service;

import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для DealContractorService
 *
 * @author Daria
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты DealContractorService")
class DealContractorServiceTest {

    @Mock
    private DealService dealService;
    @Mock
    private ContractorService contractorService;
    @InjectMocks
    private DealContractorService dealContractorService;
    private final TestHelperDeal testHelperDeal = new TestHelperDeal();

    /**
     * Проверяет корректное добавление контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - успешное добавление контрагента")
    void addContractorToDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());
        Contractor contractor = testHelperDeal.createContractor(testHelperDeal.getContractorId(), "Контрагент 1");
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), Set.of(contractor));

        when(dealService.findByIdWithContractors(testHelperDeal.getDealId())).thenReturn(deal);
        when(contractorService.findById(testHelperDeal.getContractorId())).thenReturn(contractor);
        when(dealService.save(deal)).thenReturn(savedDeal);

        Deal result = dealContractorService.addContractorToDeal(request);

        assertEquals(savedDeal, result);
        verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        verify(contractorService).findById(testHelperDeal.getContractorId());
        verify(dealService).save(deal);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - успешное удаление контрагента")
    void deleteContractorFromDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Contractor contractor = testHelperDeal.createContractor(testHelperDeal.getContractorId(), "Контрагент 1");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>(Set.of(contractor)));
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());

        when(dealService.findByIdWithContractors(testHelperDeal.getDealId())).thenReturn(deal);
        when(contractorService.findById(testHelperDeal.getContractorId())).thenReturn(contractor);
        when(dealService.save(deal)).thenReturn(savedDeal);

        Deal result = dealContractorService.deleteContractorFromDeal(request);

        assertEquals(savedDeal, result);
        verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        verify(contractorService).findById(testHelperDeal.getContractorId());
        verify(dealService).save(deal);
    }
}
