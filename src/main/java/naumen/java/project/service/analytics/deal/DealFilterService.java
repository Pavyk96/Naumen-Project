package naumen.java.project.service.analytics.deal;

import naumen.java.project.dto.analytics.deal.request.DealAnalyticsFilter;
import naumen.java.project.dto.analytics.deal.response.DateTimeRange;
import naumen.java.project.model.Deal;
import naumen.java.project.model.DealStatus;
import naumen.java.project.model.DealType;
import naumen.java.project.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки и применения фильтров аналитики сделок
 *
 * @author Daria
 */
@Service
public class DealFilterService {
    private final DealRepository dealRepository;

    public DealFilterService(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    /**
     * Применяет заданные фильтры к списку сделок и возвращает отфильтрованный результат
     * Если фильтры не заданы, возвращает все сделки
     */
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

    /**
     * Преобразует строковые типы сделок из фильтра в перечисления {@link DealType}
     * Использует все доступные типы, если фильтр пуст
     */
    private List<DealType> getDealTypes(DealAnalyticsFilter filters) {
        return filters.types() != null && !filters.types().isEmpty()
                ? filters.types().stream().map(DealType::valueOf).toList()
                : List.of(DealType.values());
    }

    /**
     * Преобразует строковые статусы сделок из фильтра в перечисления {@link DealStatus}
     * Использует все доступные статусы, если фильтр пуст
     */
    private List<DealStatus> getDealStatuses(DealAnalyticsFilter filters) {
        return filters.statuses() != null && !filters.statuses().isEmpty()
                ? filters.statuses().stream().map(DealStatus::valueOf).toList()
                : List.of(DealStatus.values());
    }

    /**
     * Определяет диапазон дат и времени на основе входных фильтров
     * Использует широкие диапазоны по умолчанию, если конкретные даты не указаны
     */
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

    /**
     * Преобразует строковое представление даты
     */
    private LocalDateTime parseDateTime(String dateStr, boolean isFrom) {
        if (dateStr == null) {
            return isFrom ? LocalDateTime.of(1900, 1, 1, 0, 0) : LocalDateTime.now();
        }
        return LocalDateTime.parse(dateStr + (isFrom ? "T00:00:00" : "T23:59:59"));
    }

    /**
     * Преобразует строковое представление даты
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null) {
            return dateStr == null ? LocalDate.of(1900, 1, 1) : LocalDate.now();
        }
        return LocalDate.parse(dateStr);
    }
}

