package naumen.java.project.mapper;

import naumen.java.project.dto.contractor.ContractorInfoForDealDTO;
import naumen.java.project.dto.deal.DealResponseDTO;
import naumen.java.project.dto.deal.DealShortResponseDTO;
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
    public DealShortResponseDTO toShortResponse(Deal entity) {
        return new DealShortResponseDTO(
                entity.getId(),
                entity.getStatus()
        );
    }

    /** Конвертация сущности в детальный DTO-ответ */
    public DealResponseDTO toDetailResponse(Deal entity) {
        return new DealResponseDTO(
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
    public List<DealResponseDTO> toListResponse(List<Deal> entities) {
        return entities.stream()
                .map(this::toDetailResponse)
                .toList();
    }

    /** Преобразует контрагентов из сделки в DTO-ответ*/
    private List<ContractorInfoForDealDTO> toListResponse(Set<Contractor> contractors) {
        if (contractors == null) {
            return List.of();
        }
        return contractors.stream()
                .map(contractor -> new ContractorInfoForDealDTO(
                        contractor.getId(),
                        contractor.getName()
                ))
                .toList();
    }
}
