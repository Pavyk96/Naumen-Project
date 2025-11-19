package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для управления связями сделка-контрагент
 *
 * @author Daria
 */
@Service
public class DealContractorBindingService {

    private final DealService dealService;
    private final ContractorService contractorService;

    public DealContractorBindingService(DealService dealService,
                                        ContractorService contractorService) {
        this.dealService = dealService;
        this.contractorService = contractorService;
    }

    /**
     * Добавляет контрагента к сделке
     */
    public Deal addContractorToDeal(String contractorId, UUID dealId) throws ResourceNotFoundException {
        Deal deal = dealService.findByIdWithContractors(dealId);
        Contractor contractor = contractorService.findById(contractorId);

        if (!isAlreadyExists(deal, contractorId)) {
            deal.addContractor(contractor);
            return dealService.save(deal);
        }
        throw new IllegalStateException(
                "Нельзя добавить контрагента с id = "
                        + contractorId + ", так как уже существует связь"
        );
    }

    /**
     * Удаляет контрагента из сделки
     */
    public Deal deleteContractorFromDeal(String contractorId, UUID dealId) throws ResourceNotFoundException {
        Deal deal = dealService.findByIdWithContractors(dealId);
        Contractor contractor = contractorService.findById(contractorId);

        if (isAlreadyExists(deal, contractorId)) {
            deal.removeContractor(contractor);
            return dealService.save(deal);
        }
        throw new ResourceNotFoundException("Контрагент", contractorId);
    }

    /**
     * Проверка на существование контрагента в сделке
     */
    private boolean isAlreadyExists(Deal deal, String contractorId) {
        return deal.getContractors().stream()
                .anyMatch(c -> c.getId().equals(contractorId));
    }
}
