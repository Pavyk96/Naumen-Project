package naumen.java.project.factory;

import naumen.java.project.dto.DealContractorRequestDTO;
import naumen.java.project.dto.deal.DealRequestDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
import naumen.java.project.model.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Фабрика тестовых данных для сущности Deal и его DTO
 *
 * @author Daria
 */
public class DealTestFactory {

    private final static UUID DEAL_ID = UUID.fromString("8e13d5a0-4298-49f3-a262-ea77ec628ac3");
    private final static UUID CONTRACTOR_ID = UUID.fromString("4a4104e1-5267-4ce1-a746-d0db8447607d");;
    private final static String DESCRIPTION = "Сделка №1";
    private final static String AGREEMENT_NUMBER = "001-01";
    private final static LocalDate AGREEMENT_DATE = LocalDate.of(2023, 1, 1);
    private final static LocalDateTime OPENED_AT = LocalDateTime.of(2023, 1, 2, 10, 0);
    private final static LocalDateTime CLOSED_AT = LocalDateTime.of(2023, 12, 31, 18, 0);

    /** Создает сущность Deal */
    public Deal createDeal(UUID id, String description, DealStatus status, Set<Contractor> contractors) {
        Deal deal = new Deal(
                id,
                description,
                AGREEMENT_NUMBER,
                AGREEMENT_DATE,
                OPENED_AT,
                CLOSED_AT,
                DealType.CREDIT,
                status
        );
        deal.setContractors(contractors);
        return deal;
    }

    /** Создает сущность Deal (упрощенная версия) */
    public Deal createDeal(UUID id, String description, DealStatus status) {
        return createDeal(id, description, status, new HashSet<>());
    }

    /** Создает сущность Deal (упрощенная версия) */
    public Deal createDeal(UUID id, Set<Contractor> contractors) {
        return createDeal(id, DESCRIPTION, DealStatus.DRAFT, contractors);
    }

    /**
     * Создает сущность Contractor
     * С помощью ReflectionTestUtils устанавливаем id
     */
    public Contractor createContractor(String name) {
        Contractor contractor =  new Contractor(name, new Country("RU", "Россия"),
                new Industry(1L, "IT"), new OrgForm("LLC", "ООО"));
        ReflectionTestUtils.setField(contractor, "id", CONTRACTOR_ID);
        return contractor;
    }

    /**
     * Создает DealRequestDTO
     */
    public DealRequestDTO createDealRequest(String id, String description, DealStatus status) {
        return new DealRequestDTO(
                id,
                description,
                AGREEMENT_NUMBER,
                AGREEMENT_DATE.toString(),
                OPENED_AT.toString(),
                CLOSED_AT.toString(),
                DealType.CREDIT.name(),
                status != null ? status.name() : null
        );
    }

    /**
     * Создает DealRequestDTO (упрощенная версия)
     */
    public DealRequestDTO createDealRequest(UUID id, String description) {
        return createDealRequest(
                id != null ? id.toString() : null,
                description,
                DealStatus.DRAFT
        );
    }

    /**
     * Создает DealResponseDTO
     */
    public DealResponseDTO createDealResponse(UUID id, String description, DealStatus status) {
        return new DealResponseDTO(
                id,
                description,
                AGREEMENT_NUMBER,
                AGREEMENT_DATE.toString(),
                OPENED_AT.toString(),
                null,
                DealType.CREDIT.getDisplayName(),
                status.getDisplayName(),
                List.of()
        );
    }

    /**
     * Создает DealShortResponseDTO
     */
    public DealShortResponseDTO createDealShortResponse(UUID id, DealStatus status) {
        return new DealShortResponseDTO(id, status);
    }

    /**
     * Создает DealContractorRequestDTO
     */
    public DealContractorRequestDTO createDealContractorRequest(UUID dealId, UUID contractorId) {
        return new DealContractorRequestDTO(
                dealId.toString(),
                contractorId.toString()
        );
    }

    /**
     * Возвращает идентификатор сделки
     */
    public UUID getDealId() {
        return DEAL_ID; }

    /**
     * Возвращает идентификатор контрагента
     */
    public UUID getContractorId() {
        return CONTRACTOR_ID;
    }

    /**
     * Возвращает описание сделки
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Возвращает номер соглашения
     */
    public String getAgreementNumber() {
        return AGREEMENT_NUMBER;
    }

    /**
     * Возвращает дату соглашения
     */
    public LocalDate getAgreementDate() {
        return AGREEMENT_DATE; }

    /**
     * Возвращает дату и время открытия сделки
     */
    public LocalDateTime getOpenedAt() {
        return OPENED_AT; }

    /**
     * Возвращает дату и время закрытия сделки
     */
    public LocalDateTime getClosedAt() { return CLOSED_AT;
    }

    /**
     * Возвращает тип сделки
     */
    public DealType getDealType() {
        return DealType.CREDIT;
    }

    /**
     * Возвращает статус сделки
     */
    public DealStatus getDealStatus() {
        return DealStatus.DRAFT;
    }
}
