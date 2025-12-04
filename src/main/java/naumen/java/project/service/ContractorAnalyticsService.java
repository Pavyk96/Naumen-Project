package naumen.java.project.service;

import naumen.java.project.dto.analytics.AnalyticsDateRange;
import naumen.java.project.dto.analytics.LocalDateRange;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsRequest;
import naumen.java.project.dto.analytics.contractor.response.*;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.repository.ContractorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Аналитика по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorAnalyticsService {

    private final ContractorRepository contractorRepository;

    public ContractorAnalyticsService(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    /**
     * Строит отчёт по контрагентам
     */
    public ContractorAnalyticsResponse analyze(ContractorAnalyticsRequest request) {
        List<Contractor> contractors = contractorRepository.findAllWithDeals();
        List<Contractor> filteredContractors = applyFilters(contractors, request.filters());
        ContractorAnalyticsSummary summary = buildSummary(filteredContractors);
        List<ContractorAnalyticsBreakdown> breakdowns =
                buildBreakdowns(filteredContractors, request.dimensions());
        ContractorAnalyticsTrends trends = request.includeTrends()
                ? buildTrends(filteredContractors)
                : null;
        return new ContractorAnalyticsResponse(summary, breakdowns, trends);
    }

    /**
     * Применяет фильтры запроса
     */
    private List<Contractor> applyFilters(
            List<Contractor> contractors,
            ContractorAnalyticsFilters filters
    ) {
        if (filters == null) {
            return contractors;
        }

        return contractors.stream()
                .filter(contractor -> filterByCountries(contractor, filters.countries()))
                .filter(contractor -> filterByIndustries(contractor, filters.industries()))
                .filter(contractor -> filterByCreateDate(contractor, filters.dateRange()))
                .toList();
    }

    /**
     * Проверяет фильтр по странам
     */
    private boolean filterByCountries(Contractor contractor, List<String> countryIds) {
        if (countryIds == null || countryIds.isEmpty()) {
            return true;
        }
        return countryIds.contains(contractor.getCountry().getId());
    }

    /**
     * Проверяет фильтр по индустриям
     */
    private boolean filterByIndustries(Contractor contractor, List<Long> industryIds) {
        if (industryIds == null || industryIds.isEmpty()) {
            return true;
        }
        return industryIds.contains(contractor.getIndustry().getId());
    }

    /**
     * Проверяет фильтр по дате создания
     */
    private boolean filterByCreateDate(Contractor contractor, AnalyticsDateRange analyticsDateRange) {
        if (analyticsDateRange == null || analyticsDateRange.createDate() == null) {
            return true;
        }
        if (contractor.getCreateDate() == null) {
            return false;
        }

        LocalDateRange createDateRange = analyticsDateRange.createDate();
        LocalDate contractorCreateDate = contractor.getCreateDate().toLocalDate();

        boolean fromMatches = createDateRange.from() == null
                || !contractorCreateDate.isBefore(LocalDate.parse(createDateRange.from()));
        boolean toMatches = createDateRange.to() == null
                || !contractorCreateDate.isAfter(LocalDate.parse(createDateRange.to()));

        return fromMatches && toMatches;
    }

    /**
     * Строит сводные метрики
     */
    private ContractorAnalyticsSummary buildSummary(List<Contractor> contractors) {
        long totalContractors = contractors.size();

        long totalDeals = contractors.stream()
                .mapToLong(contractor -> contractor.getDeals().size())
                .sum();

        long activeContractors = contractors.stream()
                .filter(contractor -> contractor.getDeals().stream().anyMatch(this::isActiveDeal))
                .count();

        double averageDealsPerContractor = totalContractors == 0
                ? 0
                : (double) totalDeals / totalContractors;

        return new ContractorAnalyticsSummary(
                totalContractors,
                averageDealsPerContractor,
                activeContractors
        );
    }

    /**
     * Проверяет активность сделки
     */
    private boolean isActiveDeal(Deal deal) {
        return deal.getStatus() == DealStatus.ACTIVE;
    }

    /**
     * Строит список разрезов
     */
    private List<ContractorAnalyticsBreakdown> buildBreakdowns(
            List<Contractor> contractors,
            List<String> dimensions
    ) {
        if (dimensions == null || dimensions.isEmpty()) {
            return List.of();
        }

        List<ContractorAnalyticsBreakdown> breakdowns = new ArrayList<>();

        for (String dimension : dimensions) {
            switch (dimension) {
                case "country" -> breakdowns.add(buildCountryBreakdown(contractors));
                case "industry" -> breakdowns.add(buildIndustryBreakdown(contractors));
                case "org_form" -> breakdowns.add(buildOrgFormBreakdown(contractors));
                case "create_year" -> breakdowns.add(buildCreateYearBreakdown(contractors));
                default -> { }
            }
        }

        return breakdowns;
    }

    /**
     * Строит разрез по странам
     */
    private ContractorAnalyticsBreakdown buildCountryBreakdown(List<Contractor> contractors) {
        var contractorsByCountry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getCountry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByCountry.entrySet().stream()
                .map(entry -> {
                    var country = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics metrics = calculateMetrics(groupContractors);

                    Map<String, Object> group = Map.of(
                            "id", country.getId(),
                            "name", country.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, metrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("country", breakdownData);
    }

    //TODO: обработка если айди не корректный
    /**
     * Строит разрез по индустриям
     */
    private ContractorAnalyticsBreakdown buildIndustryBreakdown(List<Contractor> contractors) {
        var contractorsByIndustry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getIndustry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByIndustry.entrySet().stream()
                .map(entry -> {
                    var industry = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics metrics = calculateMetrics(groupContractors);

                    Map<String, Object> group = Map.of(
                            "id", industry.getId(),
                            "name", industry.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, metrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("industry", breakdownData);
    }

    /**
     * Строит разрез по ОПФ
     */
    private ContractorAnalyticsBreakdown buildOrgFormBreakdown(List<Contractor> contractors) {
        var contractorsByOrgForm = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getOrgForm));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByOrgForm.entrySet().stream()
                .map(entry -> {
                    var orgForm = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics metrics = calculateMetrics(groupContractors);

                    Map<String, Object> group = Map.of(
                            "id", orgForm.getId(),
                            "name", orgForm.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, metrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("org_form", breakdownData);
    }

    /**
     * Строит разрез по году создания
     */
    private ContractorAnalyticsBreakdown buildCreateYearBreakdown(List<Contractor> contractors) {
        var contractorsByYear = contractors.stream()
                .collect(Collectors.groupingBy(contractor -> contractor.getCreateDate() == null
                        ? -1
                        : contractor.getCreateDate().getYear()
                ));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByYear.entrySet().stream()
                .map(entry -> {
                    Integer year = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics metrics = calculateMetrics(groupContractors);

                    Map<String, Object> group = Map.of("year", year);

                    return new ContractorAnalyticsBreakdownData(group, metrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("create_year", breakdownData);
    }

    /**
     * Считает метрики группы
     */
    private ContractorAnalyticsMetrics calculateMetrics(List<Contractor> contractors) {
        long dealCount = contractors.stream()
                .mapToLong(contractor -> contractor.getDeals().size())
                .sum();

        long activeDealCount = contractors.stream()
                .flatMap(contractor -> contractor.getDeals().stream())
                .filter(this::isActiveDeal)
                .count();

        return new ContractorAnalyticsMetrics(dealCount, activeDealCount);
    }

    /**
     * Строит тренды по месяцам
     */
    private ContractorAnalyticsTrends buildTrends(List<Contractor> contractors) {
        var contractorsByMonth = contractors.stream()
                .filter(contractor -> contractor.getCreateDate() != null)
                .collect(Collectors.groupingBy(
                        contractor -> YearMonth.from(contractor.getCreateDate()),
                        Collectors.counting()
                ));

        List<ContractorMonthlyGrowth> monthlyGrowth = contractorsByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ContractorMonthlyGrowth(
                        entry.getKey().toString(),
                        entry.getValue()
                ))
                .toList();

        return new ContractorAnalyticsTrends(monthlyGrowth);
    }
}
