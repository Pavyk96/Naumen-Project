package naumen.java.project.dto.export;

/**
 * Настройки экспорта
 * @param filename имя файла без расширения
 *
 * @author Daniil Mezev
 */
public record ExportConfig(
        String filename
) { }
