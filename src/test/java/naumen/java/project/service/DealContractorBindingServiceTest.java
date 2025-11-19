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

    private static final DealTestFactory DEAL_TEST_FACTORY = new DealTestFactory();
    private static final UUID DEAL_ID = DEAL_TEST_FACTORY.getDealId();
    private static final String CONTRACTOR_ID = DEAL_TEST_FACTORY.getContractorId();
    private static final String NON_EXISTENT_CONTRACTOR_ID = "NON_EXISTENT_CONTRACTOR";
    private static final Contractor CONTRACTOR = DEAL_TEST_FACTORY.createContractor(CONTRACTOR_ID, "Контрагент 1");
    private static final Deal EMPTY_DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID, new HashSet<>());
    private static final Deal DEAL_WITH_CONTRACTOR = DEAL_TEST_FACTORY.createDeal(DEAL_ID, new HashSet<>(Set.of(CONTRACTOR)));
    private static final Deal SAVED_DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID, Set.of(CONTRACTOR));

    /**
     * Проверяет корректное добавление контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - успешное добавление контрагента")
    void addContractorToDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(EMPTY_DEAL);
        Mockito.when(contractorService.findById(CONTRACTOR_ID)).thenReturn(CONTRACTOR);
        Mockito.when(dealService.save(EMPTY_DEAL)).thenReturn(SAVED_DEAL);

        Deal result = dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID);

        Assertions.assertEquals(SAVED_DEAL, result);
        Mockito.verify(dealService).save(EMPTY_DEAL);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - успешное удаление контрагента")
    void deleteContractorFromDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(DEAL_WITH_CONTRACTOR);
        Mockito.when(contractorService.findById(CONTRACTOR_ID)).thenReturn(CONTRACTOR);
        Mockito.when(dealService.save(DEAL_WITH_CONTRACTOR)).thenReturn(EMPTY_DEAL);

        Deal result = dealContractorBindingService.deleteContractorFromDeal(CONTRACTOR_ID, DEAL_ID);

        Assertions.assertEquals(EMPTY_DEAL, result);
        Mockito.verify(dealService).save(DEAL_WITH_CONTRACTOR);
    }

    /**
     * Проверяет выброс исключения при добавлении уже существующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при добавлении существующего контрагента")
    void addContractorToDealAlreadyExistsTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(DEAL_WITH_CONTRACTOR);
        Mockito.when(contractorService.findById(CONTRACTOR_ID)).thenReturn(CONTRACTOR);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Нельзя добавить контрагента с id = " + CONTRACTOR_ID + ", так как уже существует связь",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при удалении несуществующего контрагента")
    void deleteContractorFromDealNonExistentTest() throws ResourceNotFoundException {
        Contractor non_existent_contractor = DEAL_TEST_FACTORY.createContractor(NON_EXISTENT_CONTRACTOR_ID, "Контрагент 2");

        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(EMPTY_DEAL);
        Mockito.when(contractorService.findById(NON_EXISTENT_CONTRACTOR_ID)).thenReturn(non_existent_contractor);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(NON_EXISTENT_CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Контрагент с id = " + NON_EXISTENT_CONTRACTOR_ID + " не найдена", exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при добавлении контрагента к несуществующей сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующей сделке")
    void addContractorToNonExistentDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID))
                .thenThrow(new ResourceNotFoundException("Сделка", DEAL_ID.toString()));

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealContractorBindingService.addContractorToDeal(CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Сделка с id = " + DEAL_ID + " не найдена", exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при добавлении несуществующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующем контрагенте")
    void addNonExistentContractorToDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(EMPTY_DEAL);
        Mockito.when(contractorService.findById(NON_EXISTENT_CONTRACTOR_ID))
                .thenThrow(new ResourceNotFoundException("Контрагент", NON_EXISTENT_CONTRACTOR_ID));

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealContractorBindingService.addContractorToDeal(NON_EXISTENT_CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Контрагент с id = " + NON_EXISTENT_CONTRACTOR_ID + " не найдена", exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении контрагента из несуществующей сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующей сделке")
    void deleteContractorFromNonExistentDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID))
                .thenThrow(new ResourceNotFoundException("Сделка", DEAL_ID.toString()));

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Сделка с id = " + DEAL_ID.toString() + " не найдена", exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующем контрагенте")
    void deleteNonExistentContractorFromDealTest() throws ResourceNotFoundException {
        Mockito.when(dealService.findByIdWithContractors(DEAL_ID)).thenReturn(EMPTY_DEAL);
        Mockito.when(contractorService.findById(NON_EXISTENT_CONTRACTOR_ID))
                .thenThrow(new ResourceNotFoundException("Контрагент", NON_EXISTENT_CONTRACTOR_ID));

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(NON_EXISTENT_CONTRACTOR_ID, DEAL_ID)
        );

        Assertions.assertEquals("Контрагент с id = " + NON_EXISTENT_CONTRACTOR_ID + " не найдена", exception.getMessage());
    }
}