package naumen.java.project.service.analytics.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис построения сводных метрик по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorSummaryService {

    /**
     * Построить сводные метрики по списку контрагентов
     *
     * @param contractors список контрагентов
     * @return сводный объект метрик
     */
    public ContractorAnalyticsSummary buildSummary(List<Contractor> contractors) {
        long totalContractors = contractors.size();

        long totalDeals = contractors.stream()
                .mapToLong(contractor -> contractor.getDeals().size())
                .sum();

        long activeContractors = contractors.stream()
                .filter(contractor -> contractor.getDeals().stream().anyMatch(Deal::isActive))
                .count();

        double averageDealsPerContractor = totalContractors == 0
                ? 0.0
                : (double) totalDeals / totalContractors;

        return new ContractorAnalyticsSummary(
                totalContractors,
                averageDealsPerContractor,
                activeContractors
        );
    }
}
