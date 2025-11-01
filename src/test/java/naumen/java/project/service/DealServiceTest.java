package naumen.java.project.service;

import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.mapper.DealMapper;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.DealRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    private final TestHelperDeal testHelperDeal = new TestHelperDeal();

    /**
     * Проверяет корректное сохранение сделки
     */
    @Test
    @DisplayName("save - сохранение сделки")
    void saveTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        when(repository.save(deal)).thenReturn(deal);

        Deal result = dealService.save(deal);

        assertEquals(deal, result);
        verify(repository).save(deal);
    }

    /**
     * Проверяет корректное получение сделки по идентификатору
     */
    @Test
    @DisplayName("findById - получение сделки")
    void findByIdTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        when(repository.findById(testHelperDeal.getDealId())).thenReturn(Optional.of(deal));

        Deal result = dealService.findById(testHelperDeal.getDealId());

        assertEquals(deal, result);
        verify(repository).findById(testHelperDeal.getDealId());
    }

    /**
     * Проверяет корректное получение всех сделок
     */
    @Test
    @DisplayName("findAll - получение всех сделок")
    void findAllTest() {
        List<Deal> deals = List.of(
                testHelperDeal.createDeal(testHelperDeal.getDealId(), "Сделка 1", DealStatus.DRAFT),
                testHelperDeal.createDeal(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE)
        );
        when(repository.findAll()).thenReturn(deals);

        List<Deal> result = dealService.findAll();

        assertEquals(2, result.size());
        assertEquals(deals, result);
        verify(repository).findAll();
    }

    /**
     * Проверяет корректное создание новой сделки
     */
    @Test
    @DisplayName("createOrUpdate - создание новой сделки")
    void createTest() {
        DealRequest request = testHelperDeal.createDealRequest(null, testHelperDeal.getDescription());
        Deal newDeal = testHelperDeal.createDeal(null, testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        when(mapper.toEntity(request)).thenReturn(newDeal);
        when(repository.save(newDeal)).thenReturn(savedDeal);

        Deal result = dealService.createOrUpdate(request);

        assertEquals(savedDeal, result);
        verify(mapper).toEntity(request);
        verify(repository).save(newDeal);
        verify(repository, never()).findById(any());
    }

    /**
     * Проверяет корректное обновление существующей сделки
     */
    @Test
    @DisplayName("createOrUpdate - обновление существующей сделки")
    void updateTest() {
        DealRequest request = testHelperDeal.createDealRequest(UUID.fromString(testHelperDeal.getDealId().toString()), "Новое описание");
        Deal existingDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Старое описание", DealStatus.DRAFT);
        Deal updatedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Новое описание", DealStatus.ACTIVE);
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Новое описание", DealStatus.ACTIVE);

        when(repository.findById(testHelperDeal.getDealId())).thenReturn(Optional.of(existingDeal));
        when(mapper.toEntity(existingDeal, request)).thenReturn(updatedDeal);
        when(repository.save(updatedDeal)).thenReturn(savedDeal);

        Deal result = dealService.createOrUpdate(request);

        assertEquals(savedDeal, result);
        verify(repository).findById(testHelperDeal.getDealId());
        verify(mapper).toEntity(existingDeal, request);
        verify(repository).save(updatedDeal);
    }

    /**
     * Проверяет корректное получение сделки с контрагентами
     */
    @Test
    @DisplayName("findByIdWithContractors - получение сделки с контрагентами")
    void findByIdWithContractorsTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        when(repository.findWithContractorsById(testHelperDeal.getDealId())).thenReturn(Optional.of(deal));

        Deal result = dealService.findByIdWithContractors(testHelperDeal.getDealId());

        assertEquals(deal, result);
        verify(repository).findWithContractorsById(testHelperDeal.getDealId());
    }

    /**
     * Проверяет корректное получение всех сделок с контрагентами
     */
    @Test
    @DisplayName("findAllWithContractors - получение всех сделок с контрагентами")
    void findAllWithContractorsTest() {
        List<Deal> deals = List.of(
                testHelperDeal.createDeal(testHelperDeal.getDealId(), "Сделка 1", DealStatus.DRAFT),
                testHelperDeal.createDeal(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE)
        );
        when(repository.findAllWithContractors()).thenReturn(deals);

        List<Deal> result = dealService.findAllWithContractors();

        assertEquals(2, result.size());
        assertEquals(deals, result);
        verify(repository).findAllWithContractors();
    }

    /**
     * Проверяет корректное изменение статуса сделки
     */
    @Test
    @DisplayName("changeStatus - изменение статуса сделки")
    void changeStatusTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), DealStatus.DRAFT);
        Deal savedDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());

        when(repository.findWithContractorsById(testHelperDeal.getDealId())).thenReturn(Optional.of(deal));
        when(repository.save(deal)).thenReturn(savedDeal);

        Deal result = dealService.changeStatus(testHelperDeal.getDealId(), testHelperDeal.getDealStatus());

        assertEquals(savedDeal, result);
        assertEquals(testHelperDeal.getDealStatus(), deal.getStatus());
        verify(repository).findWithContractorsById(testHelperDeal.getDealId());
        verify(repository).save(deal);
    }

    /**
     * Проверяет корректное удаление сделки без контрагентов
     */
    @Test
    @DisplayName("delete - удаление сделки без контрагентов")
    void deleteTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        deal.setContractors(new HashSet<>());

        when(repository.existsById(testHelperDeal.getDealId())).thenReturn(true);
        when(repository.findWithContractorsById(testHelperDeal.getDealId())).thenReturn(Optional.of(deal));

        dealService.delete(testHelperDeal.getDealId());

        verify(repository).existsById(testHelperDeal.getDealId());
        verify(repository).findWithContractorsById(testHelperDeal.getDealId());
        verify(repository).deleteById(testHelperDeal.getDealId());
    }
}
