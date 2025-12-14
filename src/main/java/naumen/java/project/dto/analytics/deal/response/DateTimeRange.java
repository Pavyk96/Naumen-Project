package naumen.java.project.dto.analytics.deal.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO для представления временных периодов
 *
 * @param openedFrom Открытие сделки ОТ
 * @param openedTo Открытие сделки ДО
 * @param agreementFrom Согласование сделки ОТ
 * @param agreementTo Согласование сделки ДО
 *
 * @author Daria
 */
public record DateTimeRange(LocalDateTime openedFrom,
                             LocalDateTime openedTo,
                             LocalDate agreementFrom,
                             LocalDate agreementTo) {}