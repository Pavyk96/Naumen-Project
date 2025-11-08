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

    public static DealType fromString(String type) {
        try {
            return valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Such type does not exist");
        }
    }
}