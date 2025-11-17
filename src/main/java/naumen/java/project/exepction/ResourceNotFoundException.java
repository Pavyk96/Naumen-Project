package naumen.java.project.exepction;

/**
 * Доменное checked-исключение "ресурс не найден" для любых сущностей
 *
 * @author Daniil Mezev
 */
public class ResourceNotFoundException extends Exception {

    private final String resourceName;
    private final String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super();
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    /**
     * Получить название ресурса
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Получить id ресурса
     */
    public String getResourceId() {
        return resourceId;
    }

}

