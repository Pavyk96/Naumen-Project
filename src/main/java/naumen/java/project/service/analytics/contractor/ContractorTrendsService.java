package naumen.java.project.service.analytics.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.dto.analytics.contractor.response.ContractorMonthlyGrowth;
import naumen.java.project.model.Contractor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис построения трендов по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorTrendsService {

    /**
     * Построить тренды роста количества контрагентов по месяцам
     *
     * @param contractors список контрагентов
     * @return объект трендов по месяцам
     */
    public ContractorAnalyticsTrends buildTrends(List<Contractor> contractors) {
        Map<YearMonth, Long> contractorsByMonth = contractors.stream()
                .filter(contractor -> contractor.getCreateDate() != null)
                .collect(Collectors.groupingBy(
                        contractor -> YearMonth.from(contractor.getCreateDate()),
                        Collectors.counting()
                ));

        List<ContractorMonthlyGrowth> monthlyGrowth = contractorsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ContractorMonthlyGrowth(
                        entry.getKey().toString(),
                        entry.getValue()
                ))
                .toList();

        return new ContractorAnalyticsTrends(monthlyGrowth);
    }
}
