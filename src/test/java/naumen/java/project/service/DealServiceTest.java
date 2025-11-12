package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.mapper.DealMapper;
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
    @Mock
    private DealMapper mapper;
    @InjectMocks
    private DealService dealService;
    private final DealTestFactory dealTestFactory = new DealTestFactory();

    /**
     * Проверяет корректное сохранение сделки
     */
    @Test
    @DisplayName("save - сохранение сделки")
    void saveTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        Mockito.when(repository.save(deal))
                .thenReturn(deal);

        Deal result = dealService.save(deal);

        Assertions.assertEquals(deal, result);
        Mockito.verify(repository).save(deal);
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("findById - получение сделки")
    void findByIdTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        Mockito.when(repository.findById(dealTestFactory.getDealId()))
                .thenReturn(Optional.of(deal));

        Deal result = dealService.findById(dealTestFactory.getDealId());

        Assertions.assertEquals(deal, result);
        Mockito.verify(repository).findById(dealTestFactory.getDealId());
    }

    /**
     * Проверяет корректное получение всех сделок
     */
    @Test
    @DisplayName("findAll - получение всех сделок")
    void findAllTest() {
        List<Deal> deals = List.of(
                dealTestFactory.createDeal(dealTestFactory.getDealId(),
                        "Сделка 1", DealStatus.DRAFT),
                dealTestFactory.createDeal(UUID.randomUUID(),
                        "Сделка 2", DealStatus.ACTIVE)
        );
        Mockito.when(repository.findAll())
                .thenReturn(deals);

        List<Deal> result = dealService.findAll();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(deals, result);
        Mockito.verify(repository).findAll();
    }

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("createOrUpdate - создание новой сделки")
    void createTest() {
        Deal newDeal = dealTestFactory.createDeal(null, dealTestFactory.getDescription(),
                dealTestFactory.getDealStatus());
        Deal savedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(repository.save(Mockito.any(Deal.class)))
                .thenReturn(savedDeal);

        Deal result = dealService.createOrUpdate(newDeal);

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(repository).save(Mockito.any(Deal.class));
        Mockito.verify(repository, Mockito.never()).findById(ArgumentMatchers.any());
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("createOrUpdate - обновление существующей сделки")
    void updateTest() {
        Deal updatedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                "Новое описание", DealStatus.ACTIVE);
        Deal savedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                "Новое описание", DealStatus.ACTIVE);

        Mockito.when(repository.existsById(dealTestFactory.getDealId()))
                .thenReturn(true);
        Mockito.when(repository.save(Mockito.any(Deal.class)))
                .thenReturn(savedDeal);

        Deal result = dealService.createOrUpdate(updatedDeal);

        Assertions.assertEquals(savedDeal, result);
        Mockito.verify(repository).existsById(dealTestFactory.getDealId());
        Mockito.verify(repository).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет корректное получение сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - получение сделки с контрагентами")
    void findByIdWithContractorsTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        Mockito.when(repository.findWithContractorsById(dealTestFactory.getDealId()))
                .thenReturn(Optional.of(deal));

        Deal result = dealService.findByIdWithContractors(dealTestFactory.getDealId());

        Assertions.assertEquals(deal, result);
        Mockito.verify(repository).findWithContractorsById(dealTestFactory.getDealId());
    }

    /**
     * Проверяет корректное получение всех сделок с контрагентами
     */
    @Test
    @DisplayName("findAllWithContractors - получение всех сделок с контрагентами")
    void findAllWithContractorsTest() {
        List<Deal> deals = List.of(
                dealTestFactory.createDeal(dealTestFactory.getDealId(),
                        "Сделка 1", DealStatus.DRAFT),
                dealTestFactory.createDeal(UUID.randomUUID(),
                        "Сделка 2", DealStatus.ACTIVE)
        );
        Mockito.when(repository.findAllWithContractors())
                .thenReturn(deals);

        List<Deal> result = dealService.findAllWithContractors();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(deals, result);
        Mockito.verify(repository).findAllWithContractors();
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("changeStatus - изменение статуса сделки")
    void changeStatusTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), DealStatus.DRAFT);
        Deal savedDeal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(repository.findWithContractorsById(dealTestFactory.getDealId()))
                .thenReturn(Optional.of(deal));
        Mockito.when(repository.save(deal)).thenReturn(savedDeal);

        Deal result = dealService.changeStatus(dealTestFactory.getDealId(),
                dealTestFactory.getDealStatus());

        Assertions.assertEquals(savedDeal, result);
        Assertions.assertEquals(dealTestFactory.getDealStatus(), deal.getStatus());
        Mockito.verify(repository).findWithContractorsById(dealTestFactory.getDealId());
        Mockito.verify(repository).save(deal);
    }

    /**
     * Проверяет корректное удаление сделки без контрагентов
     */
    @Test
    @DisplayName("delete - удаление сделки без контрагентов")
    void deleteTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());
        deal.setContractors(new HashSet<>());

        Mockito.when(repository.existsById(dealTestFactory.getDealId())).thenReturn(true);
        Mockito.when(repository.findWithContractorsById(dealTestFactory.getDealId()))
                .thenReturn(Optional.of(deal));

        dealService.delete(dealTestFactory.getDealId());

        Mockito.verify(repository).existsById(dealTestFactory.getDealId());
        Mockito.verify(repository).findWithContractorsById(dealTestFactory.getDealId());
        Mockito.verify(repository).deleteById(dealTestFactory.getDealId());
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки
     */
    @Test
    @DisplayName("findById - выброс исключения при несуществующей сделке")
    void findByIdNonExistentDealTest() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealService.findById(nonExistentId)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(repository).findById(nonExistentId);
    }

    /**
     * Проверяет выброс исключения при получении несуществующей сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - выброс исключения при несуществующей сделке")
    void findByIdWithContractorsNonExistentDealTest() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.when(repository.findWithContractorsById(nonExistentId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealService.findByIdWithContractors(nonExistentId)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(repository).findWithContractorsById(nonExistentId);
    }

    /**
     * Проверяет выброс исключения при удалении несуществующей сделки
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении несуществующей сделки")
    void deleteNonExistentDealTest() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.when(repository.existsById(nonExistentId)).thenReturn(false);

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealService.delete(nonExistentId)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(repository).existsById(nonExistentId);
        Mockito.verify(repository, Mockito.never()).findWithContractorsById(nonExistentId);
        Mockito.verify(repository, Mockito.never()).deleteById(nonExistentId);
    }

    /**
     * Проверяет выброс исключения при удалении сделки с контрагентами
     */
    @Test
    @DisplayName("delete - выброс исключения при удалении сделки с контрагентами")
    void deleteDealWithContractorsTest() {
        Deal dealWithContractors = dealTestFactory.createDeal(
                dealTestFactory.getDealId(),
                dealTestFactory.getDescription(),
                dealTestFactory.getDealStatus(),
                new HashSet<>(List.of(dealTestFactory.createContractor("CTR-001", "Контрагент 1")))
        );

        Mockito.when(repository.existsById(dealTestFactory.getDealId())).thenReturn(true);
        Mockito.when(repository.findWithContractorsById(dealTestFactory.getDealId())).thenReturn(Optional.of(dealWithContractors));

        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> dealService.delete(dealTestFactory.getDealId())
        );

        Assertions.assertEquals("Deal use in contractor", exception.getMessage());
        Mockito.verify(repository).existsById(dealTestFactory.getDealId());
        Mockito.verify(repository).findWithContractorsById(dealTestFactory.getDealId());
        Mockito.verify(repository, Mockito.never()).deleteById(dealTestFactory.getDealId());
    }

    /**
     * Проверяет выброс исключения при обновлении несуществующей сделки
     */
    @Test
    @DisplayName("createOrUpdate - выброс исключения при обновлении несуществующей сделки")
    void updateNonExistentDealTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(),
                dealTestFactory.getDescription(), dealTestFactory.getDealStatus());

        Mockito.when(repository.existsById(dealTestFactory.getDealId()))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealService.createOrUpdate(deal)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(repository).existsById(dealTestFactory.getDealId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Deal.class));
    }

    /**
     * Проверяет выброс исключения при изменении статуса несуществующей сделки
     */
    @Test
    @DisplayName("changeStatus - выброс исключения при изменении статуса несуществующей сделки")
    void changeStatusNonExistentDealTest() {
        UUID nonExistentId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Mockito.when(repository.findWithContractorsById(nonExistentId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> dealService.changeStatus(nonExistentId, DealStatus.ACTIVE)
        );

        Assertions.assertEquals("Deal not found", exception.getMessage());
        Mockito.verify(repository).findWithContractorsById(nonExistentId);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Deal.class));
    }
}
