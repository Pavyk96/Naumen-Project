package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

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
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());
        Contractor contractor = testHelperDeal.createContractor(
                testHelperDeal.getContractorId(), "Контрагент 1");
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), Set.of(contractor));

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(testHelperDeal.getContractorId()))
                .thenReturn(contractor);
        Mockito.when(dealService.save(deal))
                .thenReturn(savedDeal);

        Deal result = dealContractorService.addContractorToDeal(request);

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById(testHelperDeal.getContractorId());
        Mockito.verify(dealService).save(deal);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - успешное удаление контрагента")
    void deleteContractorFromDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Contractor contractor = testHelperDeal.createContractor(
                testHelperDeal.getContractorId(), "Контрагент 1");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                new HashSet<>(Set.of(contractor)));
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(testHelperDeal.getContractorId()))
                .thenReturn(contractor);
        Mockito.when(dealService.save(deal))
                .thenReturn(savedDeal);

        Deal result = dealContractorService.deleteContractorFromDeal(request);

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById(testHelperDeal.getContractorId());
        Mockito.verify(dealService).save(deal);
    }

    /**
     * Проверяет выброс исключения при добавлении уже существующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при добавлении существующего контрагента")
    void addContractorToDealAlreadyExistsTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());
        Contractor contractor = testHelperDeal.createContractor(
                testHelperDeal.getContractorId(), "Контрагент 1");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(),
                new HashSet<>(Set.of(contractor)));

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(testHelperDeal.getContractorId()))
                .thenReturn(contractor);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealContractorService.addContractorToDeal(request)
        );

        Assertions.assertEquals("Contractor is already exists in deal", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById(testHelperDeal.getContractorId());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при удалении несуществующего контрагента")
    void deleteContractorFromDealNonExistentTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), "NON_EXISTENT_CONTRACTOR");
        Contractor contractor = testHelperDeal.createContractor(
                "NON_EXISTENT_CONTRACTOR", "Контрагент 1");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenReturn(contractor);

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorService.deleteContractorFromDeal(request)
        );

        Assertions.assertEquals("Contractor not found in deal", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при добавлении контрагента к несуществующей сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующей сделке")
    void addContractorToNonExistentDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorService.addContractorToDeal(request)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при добавлении несуществующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующем контрагенте")
    void addNonExistentContractorToDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), "NON_EXISTENT_CONTRACTOR");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId())).thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenThrow(new EntityNotFoundException("Contractor not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorService.addContractorToDeal(request)
        );

        Assertions.assertEquals("Contractor not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении контрагента из несуществующей сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующей сделке")
    void deleteContractorFromNonExistentDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), testHelperDeal.getContractorId());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId()))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorService.deleteContractorFromDeal(request)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующем контрагенте")
    void deleteNonExistentContractorFromDealTest() {
        DealContractorRequest request = testHelperDeal.createDealContractorRequest(
                testHelperDeal.getDealId(), "NON_EXISTENT_CONTRACTOR");
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(testHelperDeal.getDealId())).thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenThrow(new EntityNotFoundException("Contractor not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorService.deleteContractorFromDeal(request)
        );

        Assertions.assertEquals("Contractor not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(testHelperDeal.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }
}
