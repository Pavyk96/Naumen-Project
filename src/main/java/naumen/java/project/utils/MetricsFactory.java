package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.model.Deal;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Фабрика для создания объектов метрик аналитики ({@link DealAnalyticsMetrics}).
 * Отвечает за вычисление конкретных запрошенных метрик на основе предоставленных списков сделок,
 * используя {@link Calculator} для сложных расчетов.
 *
 * @author Daria
 */
@Component
public class MetricsFactory {
    private final Calculator calculator;

    public MetricsFactory(Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Создает объект метрик, вычисляя только те метрики, которые были запрошены
     */
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

