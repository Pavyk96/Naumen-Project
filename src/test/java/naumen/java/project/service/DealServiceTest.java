package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.DealRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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
    private DealRepository repository;
    @InjectMocks
    private DealService dealService;

    private static final DealTestFactory DEAL_TEST_FACTORY = new DealTestFactory();
    private static final UUID DEAL_ID = DEAL_TEST_FACTORY.getDealId();
    private static final UUID NON_EXISTENT_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final String DESCRIPTION = DEAL_TEST_FACTORY.getDescription();

    private static final DealStatus DEAL_STATUS = DEAL_TEST_FACTORY.getDealStatus();
    private static final DealStatus NEW_STATUS = DealStatus.ACTIVE;

    private static final Deal DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID, DESCRIPTION, DEAL_STATUS);
    private static final Deal NEW_DEAL = DEAL_TEST_FACTORY.createDeal(null, DESCRIPTION, DEAL_STATUS);
    private static final Deal SAVED_DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID, DESCRIPTION, DEAL_STATUS);
    private static final Deal UPDATED_DEAL = DEAL_TEST_FACTORY.createDeal(DEAL_ID, "Новое описание", NEW_STATUS);

    /**
     * Проверяет корректное сохранение сделки
     */
    @Test
    @DisplayName("save - сохранение сделки")
    void saveTest() {
        Mockito.when(repository.save(DEAL)).thenReturn(DEAL);

        Deal result = dealService.save(DEAL);

        Assertions.assertEquals(DEAL, result);
        Mockito.verify(repository).save(DEAL);
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("findById - получение сделки")
    void findByIdTest() throws ResourceNotFoundException {
        Mockito.when(repository.findById(DEAL_ID)).thenReturn(Optional.of(DEAL));

        Deal result = dealService.findById(DEAL_ID);

        Assertions.assertEquals(DEAL, result);
        Mockito.verify(repository).findById(DEAL_ID);
    }

    /**
     * Проверяет корректное получение всех сделок
     */
    @Test
    @DisplayName("findAll - получение всех сделок")
    void findAllTest() {
        List<Deal> deals = List.of(
                DEAL,
                DEAL_TEST_FACTORY.createDeal(UUID.randomUUID(), "Сделка 2", NEW_STATUS)
        );
        Mockito.when(repository.findAll()).thenReturn(deals);

        List<Deal> result = dealService.findAll();

        Assertions.assertEquals(deals, result);
        Mockito.verify(repository).findAll();
    }

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("createOrUpdate - создание новой сделки")
    void createTest() throws ResourceNotFoundException {
        Mockito.when(repository.save(Mockito.any(Deal.class))).thenReturn(SAVED_DEAL);

        Deal result = dealService.createOrUpdate(NEW_DEAL);

        Assertions.assertEquals(SAVED_DEAL, result);
        Mockito.verify(repository).save(NEW_DEAL);
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("createOrUpdate - обновление существующей сделки")
    void updateTest() throws ResourceNotFoundException {
        Mockito.when(repository.existsById(DEAL_ID)).thenReturn(true);
        Mockito.when(repository.save(UPDATED_DEAL)).thenReturn(UPDATED_DEAL);

        Deal result = dealService.createOrUpdate(UPDATED_DEAL);

        Assertions.assertEquals(UPDATED_DEAL, result);
        Mockito.verify(repository).existsById(DEAL_ID);
        Mockito.verify(repository).save(UPDATED_DEAL);
    }

    /**
     * Проверяет корректное получение сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - получение сделки с контрагентами")
    void findByIdWithContractorsTest() throws ResourceNotFoundException {
        Mockito.when(repository.findWithContractorsById(DEAL_ID)).thenReturn(Optional.of(DEAL));

        Deal result = dealService.findByIdWithContractors(DEAL_ID);

        Assertions.assertEquals(DEAL, result);
        Mockito.verify(repository).findWithContractorsById(DEAL_ID);
    }

    /**
     * Проверяет корректное получение всех сделок с контрагентами
     */
    @Test
    @DisplayName("findAllWithContractors - получение всех сделок с контрагентами")
    void findAllWithContractorsTest() {
        List<Deal> deals = List.of(DEAL, UPDATED_DEAL);
        Mockito.when(repository.findAllWithContractors()).thenReturn(deals);

        List<Deal> result = dealService.findAllWithContractors();

        Assertions.assertEquals(deals, result);
        Mockito.verify(repository).findAllWithContractors();
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("changeStatus - изменение статуса сделки")
    void changeStatusTest() throws ResourceNotFoundException {
        Mockito.when(repository.findWithContractorsById(DEAL_ID)).thenReturn(Optional.of(DEAL));
        Mockito.when(repository.save(DEAL)).thenReturn(DEAL);

        Deal result = dealService.changeStatus(DEAL_ID, NEW_STATUS);

        Assertions.assertEquals(DEAL, result);
        Assertions.assertEquals(NEW_STATUS, DEAL.getStatus());
        Mockito.verify(repository).findWithContractorsById(DEAL_ID);
        Mockito.verify(repository).save(DEAL);
    }

    /**
     * Проверяет корректное удаление сделки без контрагентов
     */
    @Test
    @DisplayName("delete - удаление сделки без контрагентов")
    void deleteTest() throws ResourceNotFoundException {
        DEAL.setContractors(new HashSet<>());

        Mockito.when(repository.existsById(DEAL_ID)).thenReturn(true);
        Mockito.when(repository.findWithContractorsById(DEAL_ID)).thenReturn(Optional.of(DEAL));

        dealService.delete(DEAL_ID);

        Mockito.verify(repository).deleteById(DEAL_ID);
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки
     */
    @Test
    @DisplayName("findById - выброс исключения при несуществующей сделке")
    void findByIdNonExistentDealTest() {
        Mockito.when(repository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.findById(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найдена",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - выброс исключения при несуществующей сделке")
    void findByIdWithContractorsNonExistentDealTest() {
        Mockito.when(repository.findWithContractorsById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.findByIdWithContractors(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найдена",
                exception.getMessage());
    }

    /**
     * Проверяет выброс исключения при удалении несуществующей сделки
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении несуществующей сделки")
    void deleteNonExistentDealTest() {
        Mockito.when(repository.existsById(NON_EXISTENT_ID)).thenReturn(false);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.delete(NON_EXISTENT_ID)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найдена",
                exception.getMessage());
        Mockito.verify(repository, Mockito.never()).deleteById(NON_EXISTENT_ID);
    }

    /**
     * Проверяет выброс исключения при удалении сделки с контрагентами
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении сделки с контрагентами")
    void deleteDealWithContractorsTest() {
        Deal deal = DEAL_TEST_FACTORY.createDeal(DEAL_ID, DESCRIPTION, DEAL_STATUS,
                new HashSet<>(List.of(DEAL_TEST_FACTORY.createContractor(
                        "CTR-001", "Контрагент 1"))));

        Mockito.when(repository.existsById(DEAL_ID)).thenReturn(true);
        Mockito.when(repository.findWithContractorsById(DEAL_ID)).thenReturn(Optional.of(deal));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealService.delete(DEAL_ID)
        );

        Assertions.assertEquals("Нельзя удалить сделку с id = " + DEAL_ID +
                ", так как к ней привязаны контрагенты", exception.getMessage());
        Mockito.verify(repository, Mockito.never()).deleteById(DEAL_ID);
    }

    /**
     * Проверяет выброс исключения при обновлении несуществующей сделки
     */
    @Test
    @DisplayName("createOrUpdate - выброс исключения при обновлении несуществующей сделки")
    void updateNonExistentDealTest() {
        Mockito.when(repository.existsById(DEAL_ID)).thenReturn(false);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.createOrUpdate(UPDATED_DEAL)
        );

        Assertions.assertEquals("Сделка с id = " + DEAL_ID + " не найдена",
                exception.getMessage());
        Mockito.verify(repository, Mockito.never()).save(UPDATED_DEAL);
    }

    /**
     * Проверяет выброс исключения при изменении статуса несуществующей сделки
     */
    @Test
    @DisplayName("changeStatus - выброс исключения при изменении статуса несуществующей сделки")
    void changeStatusNonExistentDealTest() {
        Mockito.when(repository.findWithContractorsById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> dealService.changeStatus(NON_EXISTENT_ID, NEW_STATUS)
        );

        Assertions.assertEquals("Сделка с id = " + NON_EXISTENT_ID + " не найдена",
                exception.getMessage());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }
}