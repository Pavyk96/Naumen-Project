package naumen.java.project.model;

/**
 * Статус сделки
 *
 * @author Daria
 */
public enum DealStatus {
    /**
     * Черновик сделки - можно редактировать все поля
     */
    DRAFT("Черновик"),

    /**
     * Активная сделка - участвует в текущих операциях
     */
    ACTIVE("Активная"),

    /**
     * На рассмотрении - ожидает подтверждения или проверки
     */
    PENDING("На рассмотрении"),

    /**
     * Утвержденная сделка - прошла все проверки и утверждена
     */
    WON("Утвержденная"),

    /**
     * Закрыта - сделка завершена или отменена
     */
    CLOSED("Закрыта");

    private final String displayName;

    DealStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает статусы в удобном виде
     */
    public String getDisplayName() {
        return displayName;
    }

}