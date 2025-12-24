package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Стратегия декомпозиции аналитики сделок по типу сделки и статусу.
 * В текущей реализации фокусируется только на активных сделках для каждого типа.
 *
 * @author Daria
 */
@Component
public class TypeStatusBreakdownStrategy implements BreakdownStrategy {
    private final MetricsFactory metricsFactory;

    public TypeStatusBreakdownStrategy(MetricsFactory metricsFactory) {
        this.metricsFactory = metricsFactory;
    }

    @Override
    public String getDimensionName() {
        return "typeStatus";
    }

    @Override
    public List<BreakdownData> buildBreakdown(List<Deal> deals, List<String> metrics) {
        List<BreakdownData> data = new ArrayList<>();

        for (DealType type : DealType.values()) {
            // Фильтруем только активные сделки для текущего типа
            List<Deal> typeDeals = deals.stream()
                    .filter(deal -> deal.getType() == type && deal.isActive())
                    .toList();

            if (!typeDeals.isEmpty()) {
                // allDeals передается как исходный список deals для расчета successRate относительно общего числа
                DealAnalyticsMetrics resultMetrics = metricsFactory.createMetricsDeal(typeDeals, deals, metrics);
                data.add(new BreakdownData(
                        null,
                        null,
                        type.getDisplayName(),
                        DealStatus.ACTIVE.getDisplayName(),
                        resultMetrics
                ));
            }
        }

        return data;
    }
}
