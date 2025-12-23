package naumen.java.project.service.analytics.contractor;

import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.model.Contractor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис фасад построения аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorAnalyticsService {

    private final ContractorFilterService contractorFilterService;
    private final ContractorSummaryService contractorSummaryService;
    private final ContractorBreakdownService contractorBreakdownService;
    private final ContractorTrendsService contractorTrendsService;

    public ContractorAnalyticsService(
            ContractorFilterService contractorFilterService,
            ContractorSummaryService contractorSummaryService,
            ContractorBreakdownService contractorBreakdownService,
            ContractorTrendsService contractorTrendsService
    ) {
        this.contractorFilterService = contractorFilterService;
        this.contractorSummaryService = contractorSummaryService;
        this.contractorBreakdownService = contractorBreakdownService;
        this.contractorTrendsService = contractorTrendsService;
    }

    /**
     * Построить аналитику по контрагентам
     *
     * @param filters фильтры контрагентов
     * @param dimensions разрезы аналитики
     * @param metrics метрики для расчёта
     * @param includeTrends включать ли тренды
     *
     * @return агрегированная аналитика
     */
    public ContractorAnalyticsResponse analyze(
            ContractorAnalyticsFilters filters,
            List<String> dimensions,
            List<String> metrics,
            boolean includeTrends
    ) {
        List<Contractor> contractors = contractorFilterService.findContractors(filters);

        ContractorAnalyticsSummary summary = contractorSummaryService.buildSummary(contractors);

        List<ContractorAnalyticsBreakdown> breakdowns = contractorBreakdownService.buildBreakdowns(
                contractors,
                dimensions,
                metrics
        );

        ContractorAnalyticsTrends trends = includeTrends
                ? contractorTrendsService.buildTrends(contractors)
                : null;

        return new ContractorAnalyticsResponse(summary, breakdowns, trends);
    }
}
