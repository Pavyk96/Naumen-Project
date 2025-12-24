package naumen.java.project.dto.analytics.deal.response;

/**
 * DTO с информацией о временном периоде
 *
 * @param year Год
 * @param quarter Квартал (1-4)
 *
 * @author Daria
 */
public record DealAnalyticsPeriodInfo(
        Integer year,
        Integer quarter
) {}