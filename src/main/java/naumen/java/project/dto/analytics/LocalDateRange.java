package naumen.java.project.dto.analytics;

import java.time.LocalDate;

/**
 * Диапазон локальных дат [from; to]
 *
 * @param from дата начала
 * @param to дата окончания
 *
 * @author Daniil Mezev
 */
public record LocalDateRange(
        LocalDate from,
        LocalDate to
) { }
