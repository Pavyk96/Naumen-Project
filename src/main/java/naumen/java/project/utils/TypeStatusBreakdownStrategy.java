package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownTypeStatus;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
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
            List<Deal> typeDeals = deals.stream()
                    .filter(deal -> deal.getType() == type && deal.getStatus() == DealStatus.ACTIVE)
                    .toList();

            if (!typeDeals.isEmpty()) {
                DealAnalyticsMetrics resultMetrics = metricsFactory.createMetricsDeal(typeDeals, deals, metrics);
                data.add(new BreakdownTypeStatus(
                        type.getDisplayName(),
                        DealStatus.ACTIVE.getDisplayName(),
                        resultMetrics
                ));
            }
        }

        return data;
    }
}