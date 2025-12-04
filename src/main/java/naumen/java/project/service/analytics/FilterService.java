package naumen.java.project.service.analytics;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Daria
 */
@Service
public class FilterService {
    private final DealRepository dealRepository;

    public FilterService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    public List<Deal> applyFiltersDeal(DealAnalyticsFilter filters) {
        if (filters == null) {
            return dealRepository.findAll();
        }

        List<DealType> dealTypes = getDealTypes(filters);
        List<DealStatus> dealStatuses = getDealStatuses(filters);
        DateTimeRange dateRange = getDateTimeRange(filters);

        return dealRepository.findDealsWithFilters(
                dealTypes,
                dealStatuses,
                dateRange.openedFrom(),
                dateRange.openedTo(),
                dateRange.agreementFrom(),
                dateRange.agreementTo()
        );
    }

    private List<DealType> getDealTypes(DealAnalyticsFilter filters) {
        return filters.types() != null && !filters.types().isEmpty() ?
                filters.types().stream().map(DealType::valueOf).toList() :
                List.of(DealType.values());
    }

    private List<DealStatus> getDealStatuses(DealAnalyticsFilter filters) {
        return filters.statuses() != null && !filters.statuses().isEmpty() ?
                filters.statuses().stream().map(DealStatus::valueOf).toList() :
                List.of(DealStatus.values());
    }

    private DateTimeRange getDateTimeRange(DealAnalyticsFilter filters) {
        LocalDateTime openedFrom = LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime openedTo = LocalDateTime.now();
        LocalDate agreementFrom = LocalDate.of(1900, 1, 1);
        LocalDate agreementTo = LocalDate.now();

        if (filters.dateRange() != null) {
            if (filters.dateRange().openedAt() != null) {
                openedFrom = parseDateTime(filters.dateRange().openedAt().from(), true);
                openedTo = parseDateTime(filters.dateRange().openedAt().to(), false);
            }

            if (filters.dateRange().agreementDate() != null) {
                agreementFrom = parseDate(filters.dateRange().agreementDate().from());
                agreementTo = parseDate(filters.dateRange().agreementDate().to());
            }
        }

        return new DateTimeRange(openedFrom, openedTo, agreementFrom, agreementTo);
    }

    private LocalDateTime parseDateTime(String dateStr, boolean isFrom) {
        if (dateStr == null) {
            return isFrom ? LocalDateTime.of(1900, 1, 1, 0, 0) : LocalDateTime.now();
        }
        return LocalDateTime.parse(dateStr + (isFrom ? "T00:00:00" : "T23:59:59"));
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) {
            return dateStr == null ? LocalDate.of(1900, 1, 1) : LocalDate.now();
        }
        return LocalDate.parse(dateStr);
    }

    private record DateTimeRange(LocalDateTime openedFrom,
                                 LocalDateTime openedTo,
                                 LocalDate agreementFrom,
                                 LocalDate agreementTo) {}
}
