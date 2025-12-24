package naumen.java.project.utils;

import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Вспомогательный класс, содержащий логику для выполнения различных
 * математических расчетов и агрегации данных, связанных со сделками
 *
 * @author Daria
 */
@Component
public class Calculator {

    /**
     * Рассчитывает среднюю длительность сделок в днях:
     * Для закрытых сделок используется период между openedAt и closedAt;
     * Для активных сделок — период между openedAt и текущим моментом.
     */
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

    /**
     * Рассчитывает средний цикл продаж (sales cycle) в днях,
     * учитывая только сделки, у которых есть и дата открытия, и дата закрытия.
     */
    public double calculateAvgSalesCycle(List<Deal> deals) {
        return deals.stream()
                .filter(d -> d.getClosedAt() != null && d.getOpenedAt() != null)
                .mapToDouble(d -> Duration.between(d.getOpenedAt(), d.getClosedAt()).toDays())
                .average()
                .orElse(0.0);
    }

    /**
     * Рассчитывает процент выигрышей (win rate) среди всех закрытых сделок (WON и CLOSED).
     */
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
