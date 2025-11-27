package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDealDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.factory.DealTestFactory;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

        Assertions.assertEquals(dealTestFactory.getDealId(), result.id());
        Assertions.assertEquals(dealTestFactory.getDealStatus(), result.status());
    }

    /**
     * Проверяет корректную конвертацию сущности в детальный DTO без контрагентов
     */
    @Test
    @DisplayName("tolResponse - детальный DTO без контрагентов")
    void tolResponseWithoutContractorsTest() {
        Deal deal = dealTestFactory.createDeal(dealTestFactory.getDealId(), dealTestFactory.getDescription(), dealTestFactory.getDealStatus(), new HashSet<>());
        DealResponseDTO result = dealMapper.toDetailResponse(deal);

        Assertions.assertEquals(dealTestFactory.getDealId(), result.id());
        Assertions.assertEquals(dealTestFactory.getDescription(), result.description());
        Assertions.assertEquals(dealTestFactory.getAgreementNumber(), result.agreementNumber());
        Assertions.assertEquals(dealTestFactory.getAgreementDate().toString(), result.agreementDate());
        Assertions.assertEquals(dealTestFactory.getOpenedAt().toString(), result.openedAt());
        Assertions.assertEquals(dealTestFactory.getClosedAt().toString(), result.closedAt());
        Assertions.assertEquals(dealTestFactory.getDealType().getDisplayName(), result.type());
        Assertions.assertEquals(dealTestFactory.getDealStatus().getDisplayName(), result.status());
        Assertions.assertEquals(0, result.contractors().size());
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

        Assertions.assertEquals(dealTestFactory.getDealId(), result.id());
        Assertions.assertEquals(dealTestFactory.getDescription(), result.description());
        Assertions.assertEquals(dealTestFactory.getAgreementNumber(), result.agreementNumber());
        Assertions.assertEquals(dealTestFactory.getDealType().getDisplayName(), result.type());
        Assertions.assertEquals(dealTestFactory.getDealStatus().getDisplayName(), result.status());
        Assertions.assertEquals(2, result.contractors().size());

        List<String> contractorIds = result.contractors().stream()
                .map(ContractorInfoForDealDTO::id)
                .sorted()
                .toList();
        List<String> contractorNames = result.contractors().stream()
                .map(ContractorInfoForDealDTO::name)
                .sorted()
                .toList();

        Assertions.assertEquals(List.of("CTR-001", "CTR-002"), contractorIds);
        Assertions.assertEquals(List.of("Контрагент 1", "Контрагент 2"), contractorNames);
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

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Сделка 1", result.get(0).description());
        Assertions.assertEquals(DealStatus.DRAFT.getDisplayName(), result.get(0).status());
        Assertions.assertEquals("Сделка 2", result.get(1).description());
        Assertions.assertEquals(DealStatus.ACTIVE.getDisplayName(), result.get(1).status());
    }
}