package naumen.java.project.exepction;

/**
 * Доменное checked-исключение "ресурс не найден" для любых сущностей
 *
 * @author Daniil Mezev
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(resourceName + " с id = " + resourceId + " не найден(а)");
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

