package naumen.java.project.service.analytics.contractor;

import naumen.java.project.dto.analytics.AnalyticsDateRange;
import naumen.java.project.dto.analytics.LocalDateRange;
import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис фильтрации контрагентов для аналитики
 *
 * @author Daniil Mezev
 */
@Service
public class ContractorFilterService {

    private final ContractorRepository contractorRepository;

    public ContractorFilterService(ContractorRepository contractorRepository) {
        this.contractorRepository = contractorRepository;
    }

    /**
     * Вернуть всех контрагентов, подходящих под фильтры
     *
     * @param filters фильтры аналитики по контрагентам
     * @return список контрагентов
     */
    public List<Contractor> findContractors(ContractorAnalyticsFilters filters) {
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

        return contractorRepository.findWithFilters(
                countryIds,
                industryIds,
                orgFormIds,
                fromCreateDate,
                toCreateDate
        );
    }

    /**
     * Извлечь начальную дату создания из диапазона
     *
     * @param dateRange диапазон дат аналитики
     * @return дата или null, если не задана
     */
    private LocalDate extractFromCreateDate(AnalyticsDateRange dateRange) {
        if (dateRange == null || dateRange.createDate() == null) {
            return null;
        }
        LocalDateRange createDateRange = dateRange.createDate();
        return LocalDate.parse(createDateRange.from());
    }

    /**
     * Извлечь конечную дату создания из диапазона
     *
     * @param dateRange диапазон дат аналитики
     * @return дата "по" или null, если не задана
     */
    private LocalDate extractToCreateDate(AnalyticsDateRange dateRange) {
        if (dateRange == null || dateRange.createDate() == null) {
            return null;
        }
        LocalDateRange createDateRange = dateRange.createDate();
        return LocalDate.parse(createDateRange.to());
    }
}
