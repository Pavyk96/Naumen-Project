package naumen.java.project.dto.analytics;

import java.time.LocalDate;

/**
 * Диапазон локальных дат [from; to]
 *
 * @author Daniil Mezev
 */
public record LocalDateRange(
        LocalDate from,
        LocalDate to
) { }
