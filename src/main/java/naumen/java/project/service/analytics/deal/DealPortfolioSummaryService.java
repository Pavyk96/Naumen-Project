package naumen.java.project.service.analytics.deal;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPortfolioSummary;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.utils.Calculator;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис, отвечающий за расчет итоговой сводки по портфелю сделок
 * Предоставляет ключевые метрики, такие как общее количество сделок,
 * количество активных сделок, процент выигрышей и средняя длительность сделок
 *
 * @author Daria
 */
@Service
public class DealPortfolioSummaryService {
    private final Calculator calculator;

    public DealPortfolioSummaryService(Calculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Строит объект сводки портфеля на основе предоставленного списка сделок
     */
    public DealAnalyticsPortfolioSummary buildSummaryDeal(List<Deal> deals) {
        long totalDeals = deals.size();
        long activeDeals = countActiveDeals(deals);
        double winRate = calculator.calculateWinRate(deals);
        double avgRealDuration = calculator.calculateDurationDays(deals);

        return new DealAnalyticsPortfolioSummary(totalDeals, activeDeals, winRate, avgRealDuration);
    }

    /**
     * Подсчитывает количество сделок в статусе {@link DealStatus#ACTIVE} в списке
     */
    private long countActiveDeals(List<Deal> deals) {
        return deals.stream()
                .filter(Deal::isActive)
                .count();
    }
}

