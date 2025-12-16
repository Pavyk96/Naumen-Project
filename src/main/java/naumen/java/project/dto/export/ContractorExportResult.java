package naumen.java.project.dto.export;

/**
 * Результат экспорта (готовый файл и метаданные для ответа).
 *
 * @param bytes байты файла
 * @param contentType MIME тип
 * @param filename имя файла с расширением
 *
 * @author Daniil
 */
public record ContractorExportResult(
        byte[] bytes,
        String contentType,
        String filename
) { }
