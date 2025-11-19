package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Тренды контрагентов
 *
 * @author Daniil Mezev
 */
public record ContractorAnalyticsTrends(
        List<ContractorMonthlyGrowth> monthlyGrowth
) { }

