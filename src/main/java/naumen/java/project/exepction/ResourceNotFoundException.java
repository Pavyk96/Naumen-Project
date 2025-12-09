package naumen.java.project.exepction;

/**
 * Доменное исключение "ресурс не найден" для любых сущностей
 *
 * @author Daniil Mezev
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(resourceName + " с id = " + resourceId + " не найден(а)");
    }

}

