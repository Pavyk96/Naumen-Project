package naumen.java.project.dto.analytics.contractor.response;

/**
 * Сводка контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsSummary(
        long totalContractors,
        double avgDealsPerContractor,
        long activeContractors
) { }

