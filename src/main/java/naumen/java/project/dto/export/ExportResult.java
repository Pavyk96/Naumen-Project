package naumen.java.project.dto.export;

/**
 * ExportResult
 *
 * @param fileBytes содержимое файла
 * @param filename
 *
 * @author Daniil Mezev
 */
public record ExportResult(
        byte[] fileBytes,
        String filename
) { }
