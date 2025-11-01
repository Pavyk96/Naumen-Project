package naumen.java.project.helper;

import naumen.java.project.dto.DealContractorRequest;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Класс для создания тестовых моделей Deal
 *
 * @author Daria
 */
public class TestHelperDeal {

    private final UUID dealId = UUID.fromString("8e13d5a0-4298-49f3-a262-ea77ec628ac3");
    private final String contractorId = "CTR-2024-001";
    private final String description = "Сделка №1";
    private final String agreementNumber = "001-01";
    private final LocalDate agreementDate = LocalDate.of(2023, 1, 1);
    private final LocalDateTime openedAt = LocalDateTime.of(2023, 1, 2, 10, 0);
    private final LocalDateTime closedAt = LocalDateTime.of(2023, 12, 31, 18, 0);
    private final DealType dealType = DealType.CREDIT;
    private final DealStatus dealStatus = DealStatus.DRAFT;

    /** Создает сущность Deal */
    public Deal createDeal(UUID id, String description, DealStatus status, Set<Contractor> contractors) {
        Deal deal = new Deal(
                id,
                description,
                agreementNumber,
                agreementDate,
                openedAt,
                closedAt,
                dealType,
                status
        );
        if (contractors != null) {
            deal.setContractors(contractors);
        }
        return deal;
    }

    /** Создает сущность Deal (упрощенная версия) */
    public Deal createDeal(UUID id, String description, DealStatus status) {
        return createDeal(id, description, status, new HashSet<>());
    }

    /** Создает сущность Deal (упрощенная версия) */
    public Deal createDeal(UUID id, Set<Contractor> contractors) {
        return createDeal(id, description, dealStatus, contractors);
    }

    /** Создает сущность Contractor */
    public Contractor createContractor(String id, String name) {
        return new Contractor(id, name, "RU", 1L, "LLC");
    }

    /** Создает DealRequest */
    public DealRequest createDealRequest(String id, String description, DealStatus status) {
        return new DealRequest(
                id,
                description,
                agreementNumber,
                agreementDate.toString(),
                openedAt.toString(),
                closedAt.toString(),
                dealType.name(),
                status != null ? status.name() : null
        );
    }

    /** Создает DealRequest (упрощенная версия) */
    public DealRequest createDealRequest(UUID id, String description) {
        return createDealRequest(
                id != null ? id.toString() : null,
                description,
                dealStatus
        );
    }

    /** Создает DealResponse */
    public DealResponse createDealResponse(UUID id, String description, DealStatus status) {
        return new DealResponse(
                id,
                description,
                agreementNumber,
                agreementDate.toString(),
                openedAt.toString(),
                null,
                dealType.getDisplayName(),
                status.getDisplayName(),
                List.of()
        );
    }

    /** Создает DealShortResponse */
    public DealShortResponse createDealShortResponse(UUID id, DealStatus status) {
        return new DealShortResponse(id, status);
    }

    /** Создает DealContractorRequest */
    public DealContractorRequest createDealContractorRequest(UUID dealId, String contractorId) {
        return new DealContractorRequest(
                dealId.toString(),
                contractorId
        );
    }

    /**
     * Возвращает идентификатор сделки по умолчанию
     */
    public UUID getDealId() { return dealId; }

    /**
     * Возвращает идентификатор контрагента по умолчанию
     */
    public String getContractorId() { return contractorId; }

    /**
     * Возвращает описание сделки по умолчанию
     */
    public String getDescription() { return description; }

    /**
     * Возвращает номер соглашения по умолчанию
     */
    public String getAgreementNumber() { return agreementNumber; }

    /**
     * Возвращает дату соглашения по умолчанию
     */
    public LocalDate getAgreementDate() { return agreementDate; }

    /**
     * Возвращает дату и время открытия сделки по умолчанию
     */
    public LocalDateTime getOpenedAt() { return openedAt; }

    /**
     * Возвращает дату и время закрытия сделки по умолчанию
     */
    public LocalDateTime getClosedAt() { return closedAt; }

    /**
     * Возвращает тип сделки по умолчанию
     */
    public DealType getDealType() { return dealType; }

    /**
     * Возвращает статус сделки по умолчанию
     */
    public DealStatus getDealStatus() { return dealStatus; }
}
