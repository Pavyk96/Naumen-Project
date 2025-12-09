package naumen.java.project.dto.analytics.contractor.response;

import java.util.List;

/**
 * Тренды контрагентов
 * @param monthlyGrowth динамика по месяцам
 * @author Daniil Mezev
 */
public record ContractorAnalyticsTrends(
        List<ContractorMonthlyGrowth> monthlyGrowth
) { }

