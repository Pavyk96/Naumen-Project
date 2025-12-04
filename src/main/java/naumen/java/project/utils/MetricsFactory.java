package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.model.Deal;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Daria
 */
@Component
public class MetricsFactory {
    private final Calculator calculator;

    public MetricsFactory(Calculator calculator) {
        this.calculator = calculator;
    }

    public DealAnalyticsMetrics createMetricsDeal(List<Deal> deals,
                                                  List<Deal> allDeals,
                                                  List<String> requestedMetrics) {
        Long count = null;
        Double successRate = null;
        Double durationDays = null;

        if (requestedMetrics.contains("count")) {
            count = (long) deals.size();
        }

        if (requestedMetrics.contains("successRate")) {
            successRate = allDeals.isEmpty() ? 0.0 : (double) deals.size() / allDeals.size();
        }

        if (requestedMetrics.contains("durationDays")) {
            durationDays = calculator.calculateDurationDays(deals);
        }

        return new DealAnalyticsMetrics(count, successRate, durationDays);
    }
}
