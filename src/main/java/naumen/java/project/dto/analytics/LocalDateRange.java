package naumen.java.project.dto.analytics;

import jakarta.validation.constraints.Pattern;

/**
 * Диапазон локальных дат [from; to]
 *
 * @author Daniil Mezev
 */
public record LocalDateRange(
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
                message = "Поле from должно быть в формате ГГГГ-ММ-ДД с допустимыми месяцами (01-12) и днями (01-31)."
        )
        String from,
        @Pattern(
                regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
                message = "Поле to должно быть в формате ГГГГ-ММ-ДД с допустимыми месяцами (01-12) и днями (01-31)."
        )
        String to
) { }
