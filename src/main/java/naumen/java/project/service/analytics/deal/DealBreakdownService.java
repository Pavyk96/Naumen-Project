package naumen.java.project.service.analytics.deal;

import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.dto.analytics.deal.response.breakdown.DealAnalyticsBreakdown;
import naumen.java.project.model.Deal;
import naumen.java.project.utils.BreakdownStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, отвечающий за построение аналитических сводок (breakdowns)
 * Использует стратегический подход для обработки различных типов аналитики
 *
 * @author Daria
 */
@Service
public class DealBreakdownService {
    private final Map<String, BreakdownStrategy> strategies;

    public DealBreakdownService(List<BreakdownStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        BreakdownStrategy::getDimensionName,
                        Function.identity()
                ));
    }

    /**
     * Строит список аналитических сводок на основе предоставленных сделок и параметров запроса
     */
    public List<DealAnalyticsBreakdown> buildBreakdownsDeal(List<Deal> deals,
                                                            List<String> dimensions,
                                                            List<String> metrics) {
        List<DealAnalyticsBreakdown> breakdowns = new ArrayList<>();

        for (String dimension : dimensions) {
            BreakdownStrategy strategy = strategies.get(dimension);
            if (strategy != null) {
                List<BreakdownData> data = strategy.buildBreakdown(deals, metrics);
                breakdowns.add(new DealAnalyticsBreakdown(dimension, data));
            }
        }

        return breakdowns;
    }
}
