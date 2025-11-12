package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDealDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.factory.DealTestFactory;
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
    private final DealTestFactory dealTestFactory = new DealTestFactory();

    /**
     * Проверяет корректную конвертацию сущности в короткий DTO
     */
    @Test
    @DisplayName("toResponse - короткий DTO")
    void toResponseShortTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), dealTestFactory.getDescription(), dealTestFactory.getDealStatus(), null);
        DealShortResponseDTO result = dealMapper.toShortResponse(deal);

        assertEquals(dealTestFactory.getDealId(), result.id());
        assertEquals(dealTestFactory.getDealStatus(), result.status());
    }

    /**
     * Проверяет корректную конвертацию сущности в детальный DTO без контрагентов
     */
    @Test
    @DisplayName("tolResponse - детальный DTO без контрагентов")
    void tolResponseWithoutContractorsTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), dealTestFactory.getDescription(), dealTestFactory.getDealStatus(), new HashSet<>());
        DealResponseDTO result = dealMapper.toDetailResponse(deal);

        assertEquals(dealTestFactory.getDealId(), result.id());
        assertEquals(dealTestFactory.getDescription(), result.description());
        assertEquals(dealTestFactory.getAgreementNumber(), result.agreementNumber());
        assertEquals(dealTestFactory.getAgreementDate().toString(), result.agreementDate());
        assertEquals(dealTestFactory.getOpenedAt().toString(), result.openedAt());
        assertEquals(dealTestFactory.getClosedAt().toString(), result.closedAt());
        assertEquals(dealTestFactory.getDealType().getDisplayName(), result.type());
        assertEquals(dealTestFactory.getDealStatus().getDisplayName(), result.status());
        assertEquals(0, result.contractors().size());
    }

    /**
     * Проверяет корректную конвертацию сущности в детальный DTO с контрагентами
     */
    @Test
    @DisplayName("tolResponse - детальный DTO с контрагентами")
    void tolResponseWithContractorsTest() {
        Set<Contractor> contractors = Set.of(
                dealTestFactory.createContractor("CTR-001", "Контрагент 1"),
                dealTestFactory.createContractor("CTR-002", "Контрагент 2")
        );
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), dealTestFactory.getDescription(), dealTestFactory.getDealStatus(), contractors);
        DealResponseDTO result = dealMapper.toDetailResponse(deal);

        assertEquals(dealTestFactory.getDealId(), result.id());
        assertEquals(dealTestFactory.getDescription(), result.description());
        assertEquals(dealTestFactory.getAgreementNumber(), result.agreementNumber());
        assertEquals(dealTestFactory.getDealType().getDisplayName(), result.type());
        assertEquals(dealTestFactory.getDealStatus().getDisplayName(), result.status());
        assertEquals(2, result.contractors().size());

        List<String> contractorIds = result.contractors().stream()
                .map(ContractorInfoForDealDTO::id)
                .sorted()
                .toList();
        List<String> contractorNames = result.contractors().stream()
                .map(ContractorInfoForDealDTO::name)
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
                dealTestFactory.createDeal(dealTestFactory.getDealId(), "Сделка 1", DealStatus.DRAFT, new HashSet<>()),
                dealTestFactory.createDeal(UUID.randomUUID(), "Сделка 2", DealStatus.ACTIVE, new HashSet<>())
        );
        List<DealResponseDTO> result = dealMapper.toListResponse(deals);

        assertEquals(2, result.size());
        assertEquals("Сделка 1", result.get(0).description());
        assertEquals(DealStatus.DRAFT.getDisplayName(), result.get(0).status());
        assertEquals("Сделка 2", result.get(1).description());
        assertEquals(DealStatus.ACTIVE.getDisplayName(), result.get(1).status());
    }
}