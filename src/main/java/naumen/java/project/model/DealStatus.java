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

    public static DealStatus fromString(String status) {
        try {
            return valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Such status does not exist");
        }
    }
}