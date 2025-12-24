package naumen.java.project.service.analytics.contractor;

import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdownData;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsMetrics;
import naumen.java.project.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис построения разрезов аналитики по контрагентам
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorBreakdownService {

    /**
     * Строит список разрезов по указанным измерениям.
     *
     * @param contractors список контрагентов
     * @param dimensions измерения для разрезов
     * @param metrics список метрик, которые нужно рассчитать
     * @return список разрезов аналитики
     */
    public List<ContractorAnalyticsBreakdown> buildBreakdowns(
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
                default -> {
                    // неизвестное измерение - пропускаем
                }
            }
        }

        return breakdowns;
    }

    /**
     * Построить разрез по странам
     *
     * @param contractors список контрагентов
     * @param metrics список метрик
     * @return разрез аналитики по странам
     */
    private ContractorAnalyticsBreakdown buildCountryBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        Map<Country, List<Contractor>> contractorsByCountry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getCountry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByCountry.entrySet().stream()
                .map(entry -> {
                    Country country = entry.getKey();
                    List<Contractor> groupedContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupedContractors, metrics);

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
     * Построить разрез по индустриям
     *
     * @param contractors список контрагентов
     * @param metrics список метрик, которые нужно рассчитать
     * @return разрез аналитики по индустриям
     */
    private ContractorAnalyticsBreakdown buildIndustryBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        Map<Industry, List<Contractor>> contractorsByIndustry = contractors.stream()
                .collect(Collectors.groupingBy(Contractor::getIndustry));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByIndustry.entrySet().stream()
                .map(entry -> {
                    Industry industry = entry.getKey();
                    List<Contractor> groupedContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupedContractors, metrics);

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
     * Построить разрез по опф
     *
     * @param contractors список контрагентов
     * @param metrics список метрик, которые нужно рассчитать
     * @return разрез аналитики по ОПФ
     */
    private ContractorAnalyticsBreakdown buildOrgFormBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        Map<OrgForm, List<Contractor>> contractorsByOrgForm = contractors.stream()
                .filter(contractor -> contractor.getOrgForm() != null)
                .collect(Collectors.groupingBy(Contractor::getOrgForm));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByOrgForm.entrySet().stream()
                .map(entry -> {
                    OrgForm orgForm = entry.getKey();
                    List<Contractor> groupedContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupedContractors, metrics);

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
     * Строит разрез по году создания контрагентов
     *
     * @param contractors список контрагентов
     * @param metrics список метрик, которые нужно рассчитать
     * @return разрез аналитики по году создания
     */
    private ContractorAnalyticsBreakdown buildCreateYearBreakdown(
            List<Contractor> contractors,
            List<String> metrics
    ) {
        Map<Integer, List<Contractor>> contractorsByYear = contractors.stream()
                .collect(Collectors.groupingBy(contractor -> contractor.getCreateDate() == null
                        ? -1
                        : contractor.getCreateDate().getYear()
                ));

        List<ContractorAnalyticsBreakdownData> breakdownData = contractorsByYear.entrySet().stream()
                .map(entry -> {
                    Integer year = entry.getKey();
                    List<Contractor> groupedContractors = entry.getValue();

                    ContractorAnalyticsMetrics groupMetrics = calculateMetrics(groupedContractors, metrics);

                    Map<String, Object> group = Map.of("year", year);

                    return new ContractorAnalyticsBreakdownData(group, groupMetrics);
                })
                .toList();

        return new ContractorAnalyticsBreakdown("create_year", breakdownData);
    }

    /**
     * Рассчитывает метрики для группы контрагентов
     *
     * @param contractors список контрагентов группы
     * @param requestedMetrics список запрошенных метрик
     * @return объект метрик группы
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
                    .filter(Deal::isActive)
                    .count();
        }

        return new ContractorAnalyticsMetrics(dealCount, activeDealCount);
    }
}
