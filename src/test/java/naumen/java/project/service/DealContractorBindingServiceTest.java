package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
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

    /**
     * Проверяет корректное добавление контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - успешное добавление контрагента")
    void addContractorToDealTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), new HashSet<>());
        Contractor contractor = dealTestFactory.createContractor(
                dealTestFactory.getContractorId(), "Контрагент 1");
        Deal savedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(), Set.of(contractor));

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(dealTestFactory.getContractorId()))
                .thenReturn(contractor);
        Mockito.when(dealService.save(deal))
                .thenReturn(savedDeal);

        Deal result = dealContractorBindingService.addContractorToDeal(
                dealTestFactory.getContractorId(), dealTestFactory.getDealId());

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById(dealTestFactory.getContractorId());
        Mockito.verify(dealService).save(deal);
    }

    /**
     * Проверяет корректное удаление контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - успешное удаление контрагента")
    void deleteContractorFromDealTest() {
        Contractor contractor = dealTestFactory.createContractor(
                dealTestFactory.getContractorId(), "Контрагент 1");
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                new HashSet<>(Set.of(contractor)));
        Deal savedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(dealTestFactory.getContractorId()))
                .thenReturn(contractor);
        Mockito.when(dealService.save(deal))
                .thenReturn(savedDeal);

        Deal result = dealContractorBindingService.deleteContractorFromDeal(
                dealTestFactory.getContractorId(), dealTestFactory.getDealId());

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById(dealTestFactory.getContractorId());
        Mockito.verify(dealService).save(deal);
    }

    /**
     * Проверяет выброс исключения при добавлении уже существующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при добавлении существующего контрагента")
    void addContractorToDealAlreadyExistsTest() {
        Contractor contractor = dealTestFactory.createContractor(
                dealTestFactory.getContractorId(), "Контрагент 1");
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                new HashSet<>(Set.of(contractor)));

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById(dealTestFactory.getContractorId()))
                .thenReturn(contractor);

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealContractorBindingService.addContractorToDeal(
                        dealTestFactory.getContractorId(), dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Contractor is already exists in deal", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById(dealTestFactory.getContractorId());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при удалении несуществующего контрагента")
    void deleteContractorFromDealNonExistentTest() {
        Contractor contractor = dealTestFactory.createContractor(
                "NON_EXISTENT_CONTRACTOR", "Контрагент 1");
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenReturn(contractor);

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(
                        "NON_EXISTENT_CONTRACTOR", dealTestFactory.getDealId()));

        Assertions.assertEquals("Contractor not found in deal", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при добавлении контрагента к несуществующей сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующей сделке")
    void addContractorToNonExistentDealTest() {
        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorBindingService.addContractorToDeal(
                        dealTestFactory.getContractorId(), dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при добавлении несуществующего контрагента к сделке
     */
    @Test
    @DisplayName("addContractorToDeal - выброс исключения при несуществующем контрагенте")
    void addNonExistentContractorToDealTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId())).thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenThrow(new EntityNotFoundException("Contractor not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorBindingService.addContractorToDeal(
                        "NON_EXISTENT_CONTRACTOR", dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Contractor not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении контрагента из несуществующей сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующей сделке")
    void deleteContractorFromNonExistentDealTest() {
        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId()))
                .thenThrow(new EntityNotFoundException("Deal not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(
                        dealTestFactory.getContractorId(), dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService, Mockito.never()).findById(Mockito.anyString());
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при удалении несуществующего контрагента из сделки
     */
    @Test
    @DisplayName("deleteContractorFromDeal - выброс исключения при несуществующем контрагенте")
    void deleteNonExistentContractorFromDealTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), new HashSet<>());

        Mockito.when(dealService.findByIdWithContractors(dealTestFactory.getDealId())).thenReturn(deal);
        Mockito.when(contractorService.findById("NON_EXISTENT_CONTRACTOR"))
                .thenThrow(new EntityNotFoundException("Contractor not found"));

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealContractorBindingService.deleteContractorFromDeal(
                        "NON_EXISTENT_CONTRACTOR", dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Contractor not found", exception.getMessage());
        Mockito.verify(dealService).findByIdWithContractors(dealTestFactory.getDealId());
        Mockito.verify(contractorService).findById("NON_EXISTENT_CONTRACTOR");
        Mockito.verify(dealService, Mockito.never()).save(Mockito.any(Deal.class));
    }
}
