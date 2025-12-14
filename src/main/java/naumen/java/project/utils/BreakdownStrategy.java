package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.model.Deal;

import java.util.List;

/**
 * Интерфейс, определяющий стратегию декомпозиции (breakdown) аналитических данных
 *
 * @author Daria
 */
public interface BreakdownStrategy {

    /**
     * Возвращает уникальное имя измерения, которое реализует данная стратегия
     */
    String getDimensionName();

    /**
     * Строит агрегированные данные декомпозиции на основе списка сделок и списка запрашиваемых метрик
     */
    List<BreakdownData> buildBreakdown(List<Deal> deals, List<String> metrics);
}
