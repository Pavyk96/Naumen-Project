package naumen.java.project.utils;

import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Daria
 */
@Component
public class Calculator {
    public double calculateDurationDays(List<Deal> deals) {
        return deals.stream()
                .filter(d -> d.getOpenedAt() != null)
                .mapToDouble(d -> {
                    if (d.getClosedAt() != null) {
                        return Duration.between(d.getOpenedAt(), d.getClosedAt()).toDays();
                    } else {
                        return Duration.between(d.getOpenedAt(), LocalDateTime.now()).toDays();
                    }
                })
                .average()
                .orElse(0.0);
    }

    public double calculateAvgSalesCycle(List<Deal> deals) {
        return deals.stream()
                .filter(d -> d.getClosedAt() != null && d.getOpenedAt() != null)
                .mapToDouble(d -> Duration.between(d.getOpenedAt(), d.getClosedAt()).toDays())
                .average()
                .orElse(0.0);
    }

    public double calculateWinRate(List<Deal> deals) {
        List<Deal> closedDeals = deals.stream()
                .filter(d -> List.of(DealStatus.WON, DealStatus.CLOSED).contains(d.getStatus()))
                .toList();

        if (closedDeals.isEmpty()) {
            return 0.0;
        }

        long wonDeals = closedDeals.stream()
                .filter(d -> d.getStatus() == DealStatus.WON)
                .count();

        return (double) wonDeals / closedDeals.size();
    }
}