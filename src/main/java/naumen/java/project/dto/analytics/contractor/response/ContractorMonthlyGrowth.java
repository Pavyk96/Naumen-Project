package naumen.java.project.dto.analytics.contractor.response;

/**
 * Рост по месяцам
 *
 * @author Daniil Mezev
 */
public record ContractorMonthlyGrowth(
        String period,
        long newContractors
) { }

