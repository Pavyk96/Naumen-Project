package naumen.java.project.dto.analytics.contractor.response;

/**
 * Сводка контрагентов
 *
 * @param totalContractors количество контрагентов
 * @param avgDealsPerContractor среднее число сделок
 * @param activeContractors количество активных контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsSummary(
        long totalContractors,
        double avgDealsPerContractor,
        long activeContractors
) { }

