package naumen.java.project.dto.analytics.deal.response;

/**
 * DTO с базовой информацией об отрасли
 *
 * @param id Идентификатор отрасли
 * @param name Название отрасли
 *
 * @author Daria
 */
public record DealAnalyticsIndustryInfo(
        Long id,
        String name
) {}


