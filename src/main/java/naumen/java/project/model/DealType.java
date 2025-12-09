package naumen.java.project.model;

/**
 * Тип сделки
 *
 * @author Daria
 */
public enum DealType {
    /**
     * Кредитная сделка - предоставление заемных средств
     */
    CREDIT("Кредитная сделка"),

    /**
     * Дебетовая сделка - операции по списанию средств
     */
    DEBIT("Дебетовая сделка"),

    /**
     * Лизинговая сделка - финансовая аренда оборудования/имущества
     */
    LEASING("Лизинговая сделка");

    private final String displayName;

    DealType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает типы в удобном виде
     */
    public String getDisplayName() {
        return displayName;
    }

}