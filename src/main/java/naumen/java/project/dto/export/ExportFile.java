package naumen.java.project.dto.export;

/**
 * ExportResult
 *
 * @param content содержимое файла
 * @param fileName название файла
 *
 * @author Daniil Mezev
 */
public record ExportFile(
        String fileName,
        byte[] content
) {}
