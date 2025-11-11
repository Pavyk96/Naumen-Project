package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDeal;
import naumen.java.project.dto.deal.DealResponse;
import naumen.java.project.dto.deal.DealShortResponse;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import org.springframework.stereotype.Component;

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

    /** Преобразует контрагентов из сделки в DTO-ответ*/
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
}
