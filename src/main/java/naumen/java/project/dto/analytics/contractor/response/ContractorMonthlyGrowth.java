package naumen.java.project.dto.analytics.contractor.response;

/**
 * Появления новых контрагентов по месяцам
 * @param period период в формате YYYY-MM
 * @param newContractors количество новых контрагентов
 * @author Daniil Mezev
 */
public record ContractorMonthlyGrowth(
        String period,
        long newContractors
) { }

