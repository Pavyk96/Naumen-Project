package naumen.java.project.dto.analytics.contractor.response;

/**
 * Рост по месяцам
 * @param period период в формате YYYY-MM
 * @param newContractors количество новых контрагентов
 * @author Daniil Mezev
 */
public record ContractorMonthlyGrowth(
        String period,
        long newContractors
) { }

