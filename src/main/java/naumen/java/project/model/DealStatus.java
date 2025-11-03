package naumen.java.project.model;

/**
 * Статус сделки
 *
 * @author Daria
 */
public enum DealStatus {
    DRAFT("Черновик"),
    ACTIVE("Активная"),
    PENDING("На рассмотрении"),
    WON("Утвержденная"),
    CLOSED("Закрыта");

    private final String displayName;

    DealStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}