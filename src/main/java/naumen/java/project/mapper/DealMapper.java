package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDeal;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealRequest;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Маппер сущности сделки
 *
 * @author Daria
 */
@Component
public class DealMapper {

    /** Конвертация сущности в короткий DTO-ответ */
    public DealShortResponse toResponse(Deal entity) {
        return new DealShortResponse(
                entity.getId(),
                entity.getStatus()
        );
    }

    /** Конвертация сущности в детальный DTO-ответ */
    public DealResponse tolResponse(Deal entity) {
        return new DealResponse(
                entity.getId(),
                entity.getDescription(),
                entity.getAgreementNumber(),
                entity.getAgreementDate() != null ? entity.getAgreementDate().toString() : null,
                entity.getOpenedAt() != null ? entity.getOpenedAt().toString() : null,
                entity.getClosedAt() != null ? entity.getClosedAt().toString() : null,
                entity.getType().getDisplayName(),
                entity.getStatus().getDisplayName(),
                toListResponse(entity.getContractors())
        );
    }

    /** Конвертация списка сущностей в список DTO-ответов */
    public List<DealResponse> toListResponse(List<Deal> entities) {
        return entities.stream()
                .map(this::tolResponse)
                .toList();
    }

    /** Создаёт новую сущность из DTO запроса */
    public Deal toEntity(DealRequest request) {
        return new Deal(
                null,
                request.description(),
                request.agreementNumber(),
                parseLocalDate(request.agreementDate()),
                parseLocalDateTime(request.openedAt()),
                parseLocalDateTime(request.closedAt()),
                parseDealType(request.type()),
                parseDealStatus(request.status())
        );
    }

    /** Обновляет существующую сущность из DTO запроса */
    public Deal toEntity(Deal existingDeal, DealRequest request) {
        existingDeal.setDescription(request.description());
        existingDeal.setAgreementNumber(request.agreementNumber());
        existingDeal.setAgreementDate(parseLocalDate(request.agreementDate()));
        existingDeal.setOpenedAt(parseLocalDateTime(request.openedAt()));
        existingDeal.setClosedAt(parseLocalDateTime(request.closedAt()));
        existingDeal.setType(parseDealType(request.type()));
        existingDeal.setStatus(parseDealStatus(request.status()));
        return existingDeal;
    }

    /** Преобразует контрагентов из сделки в DTO */
    private List<ContractorInfoForDeal> toListResponse(Set<Contractor> contractors) {
        if (contractors == null) {
            return List.of();
        }
        return contractors.stream()
                .map(contractor -> new ContractorInfoForDeal(
                        contractor.getId(),
                        contractor.getName()
                ))
                .toList();
    }

    /** Парсит LocalDate */
    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd, got: " + dateString);
        }
    }

    /** Парсит LocalDateTime */
    private LocalDateTime parseLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.replace("Z", ""));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid datetime format. Expected: yyyy-MM-ddTHH:mm:ss, got: " + dateTimeString);
        }
    }

    /** Парсит DealType */
    private DealType parseDealType(String typeString) {
        if (typeString == null || typeString.isBlank()) {
            throw new IllegalArgumentException("type cannot be null or empty");
        }
        try {
            return DealType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid deal type: " + typeString +
                    ". Valid values: " + Arrays.toString(DealType.values()));
        }
    }

    /** Парсит DealStatus */
    private DealStatus parseDealStatus(String statusString) {
        if (statusString == null || statusString.isBlank()) {
            return DealStatus.DRAFT;
        }
        try {
            return DealStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid deal status: " + statusString +
                    ". Valid values: " + Arrays.toString(DealStatus.values()));
        }
    }
}
