package naumen.java.project.validation;

import naumen.java.project.dto.export.ExportFormat;

import java.util.EnumSet;
import java.util.Set;

/**
 * Утилиты для валидации конфигурации экспорта
 *
 * @author Daniil Mezev
 */
public class ExportFormatValidator {

    /**
     * Проверить, что для всех значений ExportFormat существует экспортер
     */
    public void validateAllFormatsSupported(Set<ExportFormat> supportedFormats) {
        EnumSet<ExportFormat> missingFormats = EnumSet.allOf(ExportFormat.class);
        missingFormats.removeAll(supportedFormats);

        if (!missingFormats.isEmpty()) {
            throw new IllegalStateException(
                    "Не реализованы экспортеры для форматов: " + missingFormats
            );
        }
    }
}
