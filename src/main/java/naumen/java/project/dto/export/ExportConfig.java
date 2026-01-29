package naumen.java.project.dto.export;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Настройки экспорта
 *
 * @param exportFormat формат экспорта
 * @param filename имя файла без расширения
 *
 * @author Daniil Mezev
 */
public record ExportConfig(
        @NotNull ExportFormat exportFormat,
        @NotBlank String filename
) {}
