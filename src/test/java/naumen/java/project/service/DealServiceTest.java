package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.DealRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Тесты для DealService
 *
 * @author Daria
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты DealService")
class DealServiceTest {

    @Mock
    private DealRepository dealRepositoryMock;
    @InjectMocks
    private DealService dealService;

    private static final UUID NON_EXISTENT_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final DealTestFactory dealTestFactory = new DealTestFactory();

    private final UUID dealId = dealTestFactory.getDealId();
    private final String description = dealTestFactory.getDescription();

    private final DealStatus dealStatus = dealTestFactory.getDealStatus();

    private final Deal deal = dealTestFactory.createDeal(dealId, description, dealStatus);

    /**
     * Проверяет корректное получение сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - получение сделки с контрагентами")
    void findByIdWithContractorsTest() throws ResourceNotFoundException {
        Mockito.when(dealRepositoryMock.findWithContractorsById(dealId)).thenReturn(Optional.of(deal));

        Deal result = dealService.findByIdWithContractors(dealId);

        Assertions.assertEquals(deal, result);
        Mockito.verify(dealRepositoryMock).findWithContractorsById(dealId);
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("changeStatus - изменение статуса сделки")
    void changeStatusTest() throws ResourceNotFoundException {
        Mockito.when(dealRepositoryMock.findWithContractorsById(dealId)).thenReturn(Optional.of(deal));
        Mockito.when(dealRepositoryMock.save(deal)).thenReturn(deal);

        Deal result = dealService.changeStatus(dealId, DealStatus.ACTIVE);

        Assertions.assertEquals(deal, result);
        Assertions.assertEquals(DealStatus.ACTIVE, deal.getStatus());
        Mockito.verify(dealRepositoryMock).findWithContractorsById(dealId);
        Mockito.verify(dealRepositoryMock).save(deal);
    }

    /**
     * Проверяет корректное удаление сделки без контрагентов
     */
    @Test
    @DisplayName("delete - удаление сделки без контрагентов")
    void deleteTest() throws ResourceNotFoundException {
        deal.setContractors(new HashSet<>());

        Mockito.when(dealRepositoryMock.existsById(dealId)).thenReturn(true);
        Mockito.when(dealRepositoryMock.findWithContractorsById(dealId)).thenReturn(Optional.of(deal));

        dealService.delete(dealId);

        Mockito.verify(dealRepositoryMock).deleteById(dealId);
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки
     */
    @Test
    @DisplayName("findById - выброс исключения при несуществующей сделке")
    void findByIdNonExistentDealTest() {
        Mockito.when(dealRepositoryMock.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.findById(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найден(а)",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - выброс исключения при несуществующей сделке")
    void findByIdWithContractorsNonExistentDealTest() {
        Mockito.when(dealRepositoryMock.findWithContractorsById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.findByIdWithContractors(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найден(а)",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении несуществующей сделки
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении несуществующей сделки")
    void deleteNonExistentDealTest() {
        Mockito.when(dealRepositoryMock.existsById(NON_EXISTENT_ID)).thenReturn(false);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.delete(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найден(а)",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении сделки с контрагентами
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении сделки с контрагентами")
    void deleteDealWithContractorsTest() {
        Deal deal = dealTestFactory.createDeal(dealId, description, dealStatus,
                new HashSet<>(List.of(dealTestFactory.createContractor(
                        "CTR-001", "Контрагент 1"))));

        Mockito.when(dealRepositoryMock.existsById(dealId)).thenReturn(true);
        Mockito.when(dealRepositoryMock.findWithContractorsById(dealId)).thenReturn(Optional.of(deal));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealService.delete(dealId)
        );

        Assertions.assertEquals("Нельзя удалить сделку с id = " + dealId +
                ", так как к ней привязаны контрагенты", exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при изменении статуса несуществующей сделки
     */
    @Test
    @DisplayName("changeStatus - выброс исключения при изменении статуса несуществующей сделки")
    void changeStatusNonExistentDealTest() {
        Mockito.when(dealRepositoryMock.findWithContractorsById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.changeStatus(NON_EXISTENT_ID, DealStatus.ACTIVE)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найден(а)",
                exception.getMessage());
    }
}