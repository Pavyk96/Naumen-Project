package naumen.java.project.utils;

import naumen.java.project.dto.analytics.deal.response.DealAnalyticsIndustryInfo;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsMetrics;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsPeriodInfo;
import naumen.java.project.dto.analytics.deal.response.breakdown.BreakdownData;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.Industry;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Стратегия декомпозиции аналитики сделок по временному периоду (квартал/год) и индустрии
 * Группирует сделки, рассчитывает метрики и форматирует результат
 *
 * @author Daria
 */
@Component
public class TimeIndustryBreakdownStrategy implements BreakdownStrategy {
    /** Количество месяцев в одном квартале */
    private static final int MONTHS_PER_QUARTER = 3;

    /** Смещение для корректного расчета квартала (перевод из 1-12 в 0-11) */
    private static final int MONTH_INDEX_OFFSET = 1;

    /** Базовое смещение для получения номера квартала */
    private static final int QUARTER_BASE_OFFSET = 1;
    private final MetricsFactory metricsFactory;

    public TimeIndustryBreakdownStrategy(MetricsFactory metricsFactory) {
        this.metricsFactory = metricsFactory;
    }

    @Override
    public String getDimensionName() {
        return "timeIndustry";
    }

    @Override
    public List<BreakdownData> buildBreakdown(List<Deal> deals, List<String> metrics) {
        List<BreakdownData> data = new ArrayList<>();

        Map<DealAnalyticsPeriodInfo, Map<Industry, List<Deal>>> groupedDeals = deals.stream()
                .filter(deal -> getPrimaryIndustry(deal) != null)
                .filter(deal -> deal.getAgreementDate() != null)
                .collect(Collectors.groupingBy(
                        this::getYearQuarter,
                        Collectors.groupingBy(this::getPrimaryIndustry)
                ));

        for (Map.Entry<DealAnalyticsPeriodInfo, Map<Industry, List<Deal>>> periodEntry : groupedDeals.entrySet()) {
            DealAnalyticsPeriodInfo periodEntryKey = periodEntry.getKey();

            for (Map.Entry<Industry, List<Deal>> industryEntry : periodEntry.getValue().entrySet()) {
                Industry industry = industryEntry.getKey();
                List<Deal> industryDeals = industryEntry.getValue();

                // allDeals передается как исходный список deals для расчета successRate относительно общего числа
                DealAnalyticsMetrics resultMetrics = metricsFactory.createMetricsDeal(industryDeals, deals, metrics);

                data.add(new BreakdownData(
                        new DealAnalyticsPeriodInfo(periodEntryKey.year(), periodEntryKey.quarter()),
                        new DealAnalyticsIndustryInfo(industry.getId(), industry.getName()),
                        null,
                        null,
                        resultMetrics
                ));
            }
        }

        return data;
    }

    /**
     * Определяет год и квартал на основе даты соглашения сделки
     */
    private DealAnalyticsPeriodInfo getYearQuarter(Deal deal) {
        LocalDate agreementDate = deal.getAgreementDate();
        int year = agreementDate.getYear();
        int quarter = (agreementDate.getMonthValue() - MONTH_INDEX_OFFSET) / MONTHS_PER_QUARTER + QUARTER_BASE_OFFSET;
        return new DealAnalyticsPeriodInfo(year, quarter);
    }
    /**
     * Извлекает основную индустрию первого контрагента сделки
     */
    private Industry getPrimaryIndustry(Deal deal) {
        return deal.getContractors().stream()
                .findFirst()
                .map(Contractor::getIndustry)
                .orElse(null);
    }
}
