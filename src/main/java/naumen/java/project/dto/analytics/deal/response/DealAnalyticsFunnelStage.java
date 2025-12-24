package naumen.java.project.dto.analytics.deal.response;

/**
 * DTO для представления отдельного этапа сделок
 *
 * @param stage Название этапа воронки
 * @param count Количество сделок на данном этапе
 * @param conversionRate Коэффициент конверсии на следующий этап
 * @param avgDurationDays Средняя длительность нахождения сделок на этом этапе
 *
 * @author Daria
 */
public record DealAnalyticsFunnelStage(
        String stage,
        Long count,
        Double conversionRate,
        Double avgDurationDays
) {}

