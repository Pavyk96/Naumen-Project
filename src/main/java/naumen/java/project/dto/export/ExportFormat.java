package naumen.java.project.dto.export;

/**
 * Форматы экспорта
 *
 * @author Daniil Mezev
 */
public enum ExportFormat {
    /**
     * Формат экспорта для EXEL
     */
    XLSX(".xlsx"),
    /**
     * Формат экспорта в PDF
     */
    PDF(".pdf");

    private final String displayName;

    ExportFormat(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает форматы в удобном виде
     */
    public String getDisplayName() {
        return displayName;
    }

}

