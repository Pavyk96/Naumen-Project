package naumen.java.project.model;

/**
 * Тип сделки
 *
 * @author Daria
 */
public enum DealType {
    CREDIT("Кредитная сделка"),
    DEBIT("Дебетовая сделка"),
    LEASING("Лизинговая сделка");

    private final String displayName;

    DealType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}