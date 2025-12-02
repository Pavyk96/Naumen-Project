package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Тесты для DealContractorBindingService
 *
 * @author Daria
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты DealContractorBindingService")
class DealContractorBindingServiceTest {

    @Mock
    private DealService dealService;
    @Mock
    private ContractorService contractorService;
    @InjectMocks
    private DealContractorBindingService dealContractorBindingService;

    private final DealTestFactory dealTestFactory = new DealTestFactory();
    private final UUID dealId = dealTestFactory.getDealId();
    private final UUID contractorId = dealTestFactory.getContractorId();

    private final Contractor contractor = dealTestFactory.createContractor("Контрагент 1");

    private final Deal emptyDeal = dealTestFactory.createDeal(dealId, new HashSet<>());
    private final Deal dealWithContractor = dealTestFactory.createDeal(dealId, new HashSet<>(Set.of(contractor)));
    private final Deal savedDeal = dealTestFactory.createDeal(dealId, Set.of(contractor));

    /**
     * Проверяет корректное добавление контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - успешное добавление контрагента")
    void addContractorToDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(dealId)).thenReturn(emptyDeal);
        Mockito.when(contractorService.findById(contractorId)).thenReturn(contractor);
        Mockito.when(dealService.save(emptyDeal)).thenReturn(savedDeal);

        Deal result = dealContractorBindingService.addContractorToDeal(contractorId, dealId);

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(dealService).save(emptyDeal);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - успешное удаление контрагента")
    void deleteContractorFromDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(dealId)).thenReturn(dealWithContractor);
        Mockito.when(contractorService.findById(contractorId)).thenReturn(contractor);
        Mockito.when(dealService.save(dealWithContractor)).thenReturn(emptyDeal);

        Deal result = dealContractorBindingService.deleteContractorFromDeal(contractorId, dealId);

        Assertions.assertEquals(emptyDeal, result);
        Mockito.verify(dealService).save(dealWithContractor);
    }

    /**
     * Проверяет выброс исключения при добавлении уже существующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при добавлении существующего контрагента")
    void addContractorToDealAlreadyExistsTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(dealId)).thenReturn(dealWithContractor);
        Mockito.when(contractorService.findById(contractorId)).thenReturn(contractor);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealContractorBindingService.addContractorToDeal(contractorId, dealId)
        );

        Assertions.assertEquals("Нельзя добавить контрагента с id = " + contractorId + ", так как уже существует связь",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при удалении несуществующего контрагента")
    void deleteContractorFromDealNonExistentTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(dealId)).thenReturn(emptyDeal);
        Mockito.when(contractorService.findById(contractorId)).thenReturn(contractor);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(contractorId, dealId)
        );

        Assertions.assertEquals("Нельзя удалить контрагента с id = " + contractorId + ", так как связь не существует",
                exception.getMessage());
    }
}