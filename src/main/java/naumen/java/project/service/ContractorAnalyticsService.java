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
        ContractorAnalyticsFilters filters = request.filters();

        List<String> countryIds = (filters != null && filters.countries() != null && !filters.countries().isEmpty())
                ? filters.countries()
                : null;

        List<Long> industryIds = (filters != null && filters.industries() != null && !filters.industries().isEmpty())
                ? filters.industries()
                : null;

        List<String> orgFormIds = (filters != null && filters.orgForms() != null && !filters.orgForms().isEmpty())
                ? filters.orgForms()
                : null;

        AnalyticsDateRange dateRange = filters != null ? filters.dateRange() : null;
        LocalDate fromCreateDate = extractFromCreateDate(dateRange);
        LocalDate toCreateDate = extractToCreateDate(dateRange);

        List<Contractor> contractors = contractorRepository.findWithFilters(
                countryIds,
                industryIds,
                orgFormIds,
                fromCreateDate,
                toCreateDate
        );

        ContractorAnalyticsSummary summary = buildSummary(contractors);
        List<ContractorAnalyticsBreakdown> breakdowns =
                buildBreakdowns(contractors, request.dimensions(), request.metrics());
        ContractorAnalyticsTrends trends = request.includeTrends()
                ? buildTrends(contractors)
                : null;

        return new ContractorAnalyticsResponse(summary, breakdowns, trends);
    }

    private LocalDate extractFromCreateDate(AnalyticsDateRange dateRange) {
        if (dateRange == null || dateRange.createDate() == null) {
            return null;
        }
        LocalDateRange createDate = dateRange.createDate();
        return LocalDate.parse(createDate.from());
    }

    private LocalDate extractToCreateDate(AnalyticsDateRange dateRange) {
        if (dateRange == null || dateRange.createDate() == null) {
            return null;
        }
        LocalDateRange createDate = dateRange.createDate();
        return LocalDate.parse(createDate.to());
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
            List<String> dimensions,
            List<String> metrics
    ) {
        if (dimensions == null || dimensions.isEmpty()) {
            return List.of();
        }

        List<ContractorAnalyticsBreakdown> breakdowns = new ArrayList<>();

        for (String dimension : dimensions) {
            switch (dimension) {
                case "country" -> breakdowns.add(buildCountryBreakdown(contractors, metrics));
                case "industry" -> breakdowns.add(buildIndustryBreakdown(contractors, metrics));
                case "org_form" -> breakdowns.add(buildOrgFormBreakdown(contractors, metrics));
                case "create_year" -> breakdowns.add(buildCreateYearBreakdown(contractors, metrics));
                default -> { }
            }
        }

        return breakdowns;
    }

    /**
     * Строит разрез по странам
     */
    private ContractorAnalyticsBreakdown buildCountryBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        var contractorsByCountry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getCountry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByCountry.entrySet().stream()
                .map(entry -> {
                    var country = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupContractors, metrics);

                    Map<String, Object> group = Map.of(
                            "id", country.getId(),
                            "name", country.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, groupMetrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("country", breakdownData);
    }

    /**
     * Строит разрез по индустриям
     */
    private ContractorAnalyticsBreakdown buildIndustryBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        var contractorsByIndustry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getIndustry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByIndustry.entrySet().stream()
                .map(entry -> {
                    var industry = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupContractors, metrics);

                    Map<String, Object> group = Map.of(
                            "id", industry.getId(),
                            "name", industry.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, groupMetrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("industry", breakdownData);
    }

    /**
     * Строит разрез по ОПФ
     */
    private ContractorAnalyticsBreakdown buildOrgFormBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        var contractorsByOrgForm = contractors.stream()
                .filter(contractor -> contractor.getOrgForm() != null)
                .collect(Collectors.groupingBy(Contractor::getOrgForm));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByOrgForm.entrySet().stream()
                .map(entry -> {
                    var orgForm = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupContractors, metrics);

                    Map<String, Object> group = Map.of(
                            "id", orgForm.getId(),
                            "name", orgForm.getName()
                    );

                    return new ContractorAnalyticsBreakdownData(group, groupMetrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("org_form", breakdownData);
    }

    /**
     * Строит разрез по году создания
     */
    private ContractorAnalyticsBreakdown buildCreateYearBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        var contractorsByYear = contractors.stream()
                .collect(Collectors.groupingBy(contractor -> contractor.getCreateDate() == null
                        ? -1
                        : contractor.getCreateDate().getYear()
                ));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByYear.entrySet().stream()
                .map(entry -> {
                    Integer year = entry.getKey();
                    List<Contractor> groupContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupContractors, metrics);

                    Map<String, Object> group = Map.of("year", year);

                    return new ContractorAnalyticsBreakdownData(group, groupMetrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("create_year", breakdownData);
    }

    /**
     * Считает метрики группы в зависимости от запрошенных metrics
     */
    private ContractorAnalyticsMetrics calculateMetrics(
            List<Contractor> contractors,
            List<String> requestedMetrics
    ) {
        boolean needCount = requestedMetrics == null
                || requestedMetrics.isEmpty()
                || requestedMetrics.contains("count");

        boolean needActive = requestedMetrics == null
                || requestedMetrics.isEmpty()
                || requestedMetrics.contains("active_deals_count");

        Long dealCount = null;
        Long activeDealCount = null;

        if (needCount) {
            dealCount = contractors.stream()
                    .mapToLong(contractor -> contractor.getDeals().size())
                    .sum();
        }

        if (needActive) {
            activeDealCount = contractors.stream()
                    .flatMap(contractor -> contractor.getDeals().stream())
                    .filter(this::isActiveDeal)
                    .count();
        }

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
