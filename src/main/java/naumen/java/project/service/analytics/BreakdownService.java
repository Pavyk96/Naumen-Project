package naumen.java.project.service.analytics;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsRequestDTO;
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
 * @author Daria
 */
@Service
public class BreakdownService {
    private final Map<String, BreakdownStrategy> strategies;

    public BreakdownService(List<BreakdownStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        BreakdownStrategy::getDimensionName,
                        Function.identity()
                ));
    }

    public List<DealAnalyticsBreakdown> buildBreakdownsDeal(List<Deal> deals,
                                                            DealAnalyticsRequestDTO request) {
        List<DealAnalyticsBreakdown> breakdowns = new ArrayList<>();

        for (String dimension : request.dimensions()) {
            BreakdownStrategy strategy = strategies.get(dimension);
            if (strategy != null) {
                List<BreakdownData> data = strategy.buildBreakdown(deals, request.metrics());
                breakdowns.add(new DealAnalyticsBreakdown(dimension, data));
            }
        }

        return breakdowns;
    }
}
