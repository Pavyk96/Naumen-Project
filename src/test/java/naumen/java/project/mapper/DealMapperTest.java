package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDeal;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.helper.TestHelperDeal;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тесты для DealMapper
 *
 * @author Daria
 */
@DisplayName("Тесты DealMapper")
class DealMapperTest {

    private final DealMapper dealMapper = new DealMapper();
    private final TestHelperDeal testHelperDeal = new TestHelperDeal();

    /**
     * Проверяет корректную конвертацию сущности в короткий DTO
     */
    @Test
    @DisplayName("toResponse - короткий DTO")
    void toResponseShortTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus(), null);
        DealShortResponse result = dealMapper.toResponse(deal);

        assertEquals(testHelperDeal.getDealId(), result.id());
        assertEquals(testHelperDeal.getDealStatus(), result.status());
    }

    /**
     * Проверяет корректную конвертацию сущности в детальный DTO без контрагентов
     */
    @Test
    @DisplayName("tolResponse - детальный DTO без контрагентов")
    void tolResponseWithoutContractorsTest() {
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus(), new HashSet<>());
        DealResponse result = dealMapper.tolResponse(deal);

        assertEquals(testHelperDeal.getDealId(), result.id());
        assertEquals(testHelperDeal.getDescription(), result.description());
        assertEquals(testHelperDeal.getAgreementNumber(), result.agreementNumber());
        assertEquals(testHelperDeal.getAgreementDate().toString(), result.agreementDate());
        assertEquals(testHelperDeal.getOpenedAt().toString(), result.openedAt());
        assertEquals(testHelperDeal.getClosedAt().toString(), result.closedAt());
        assertEquals(testHelperDeal.getDealType().getDisplayName(), result.type());
        assertEquals(testHelperDeal.getDealStatus().getDisplayName(), result.status());
        assertEquals(0, result.contractors().size());
    }

    /**
     * Проверяет корректную конвертацию сущности в детальный DTO с контрагентами
     */
    @Test
    @DisplayName("tolResponse - детальный DTO с контрагентами")
    void tolResponseWithContractorsTets() {
        Set<Contractor> contractors = Set.of(
                testHelperDeal.createContractor("CTR-001", "Контрагент 1"),
                testHelperDeal.createContractor("CTR-002", "Контрагент 2")
        );
        Deal deal = testHelperDeal.createDeal(testHelperDeal.getDealId(), testHelperDeal.getDescription(), testHelperDeal.getDealStatus(), contractors);
        DealResponse result = dealMapper.tolResponse(deal);

        assertEquals(testHelperDeal.getDealId(), result.id());
        assertEquals(testHelperDeal.getDescription(), result.description());
        assertEquals(testHelperDeal.getAgreementNumber(), result.agreementNumber());
        assertEquals(testHelperDeal.getDealType().getDisplayName(), result.type());
        assertEquals(testHelperDeal.getDealStatus().getDisplayName(), result.status());
        assertEquals(2, result.contractors().size());

        List<String> contractorIds = result.contractors().stream()
                .map(ContractorInfoForDeal::id)
                .sorted()
                .toList();
        List<String> contractorNames = result.contractors().stream()
                .map(ContractorInfoForDeal::name)
                .sorted()
                .toList();

        assertEquals(List.of("CTR-001", "CTR-002"), contractorIds);
        assertEquals(List.of("Контрагент 1", "Контрагент 2"), contractorNames);
    }

    /**
     * Проверяет корректную конвертацию списка сущностей в список DTO
     */
    @Test
    @DisplayName("toListResponse - список сущностей")
    void toListResponseTest() {
        List<Deal> deals = List.of(
                testHelperDeal.createDeal(testHelperDeal.getDealId(), "Сделка 1", DealStatus.DRAFT, new HashSet<>()),
                testHelperDeal.createDeal(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE, new HashSet<>())
        );
        List<DealResponse> result = dealMapper.toListResponse(deals);

        assertEquals(2, result.size());
        assertEquals("Сделка 1", result.get(0).description());
        assertEquals(DealStatus.DRAFT.getDisplayName(), result.get(0).status());
        assertEquals("Сделка 2", result.get(1).description());
        assertEquals(DealStatus.ACTIVE.getDisplayName(), result.get(1).status());
    }

    /**
     * Проверяет корректное создание новой сущности из DTO запроса
     */
    @Test
    @DisplayName("toEntity - для новой сущности")
    void toEntityCreateNewTest() {
        DealRequest request = testHelperDeal.createDealRequest(null, testHelperDeal.getDescription(), testHelperDeal.getDealStatus());
        Deal result = dealMapper.toEntity(request);

        assertNull(result.getId());
        assertEquals(testHelperDeal.getDescription(), result.getDescription());
        assertEquals(testHelperDeal.getAgreementNumber(), result.getAgreementNumber());
        assertEquals(testHelperDeal.getAgreementDate(), result.getAgreementDate());
        assertEquals(testHelperDeal.getOpenedAt(), result.getOpenedAt());
        assertEquals(testHelperDeal.getClosedAt(), result.getClosedAt());
        assertEquals(testHelperDeal.getDealType(), result.getType());
        assertEquals(testHelperDeal.getDealStatus(), result.getStatus());
    }

    /**
     * Проверяет корректное создание новой сущности с установкой статуса DRAFT по умолчанию
     */
    @Test
    @DisplayName("toEntity - для новой сущности со статусом по умолчанию")
    void toEntitySetStatusTest() {
        DealRequest request = testHelperDeal.createDealRequest(null, testHelperDeal.getDescription(), null);
        Deal result = dealMapper.toEntity(request);
        assertEquals(DealStatus.DRAFT, result.getStatus());
    }

    /**
     * Проверяет корректное обновление существующей сущности из DTO запроса
     */
    @Test
    @DisplayName("toEntity - обновление сущности")
    void toEntityUpdateExistingTest() {
        Deal existingDeal = testHelperDeal.createDeal(testHelperDeal.getDealId(), "Старое описание", DealStatus.DRAFT, new HashSet<>());
        DealRequest request = testHelperDeal.createDealRequest(testHelperDeal.getDealId().toString(), "Новое описание", DealStatus.WON);
        Deal result = dealMapper.toEntity(existingDeal, request);

        assertEquals(testHelperDeal.getDealId(), result.getId());
        assertEquals("Новое описание", result.getDescription());
        assertEquals(testHelperDeal.getAgreementNumber(), result.getAgreementNumber());
        assertEquals(testHelperDeal.getAgreementDate(), result.getAgreementDate());
        assertEquals(testHelperDeal.getOpenedAt(), result.getOpenedAt());
        assertEquals(testHelperDeal.getClosedAt(), result.getClosedAt());
        assertEquals(testHelperDeal.getDealType(), result.getType());
        assertEquals(DealStatus.WON, result.getStatus());
    }
}