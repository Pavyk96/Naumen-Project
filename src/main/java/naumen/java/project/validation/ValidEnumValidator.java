package naumen.java.project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Валидатор для проверки корректности значений enum
 *
 * @author Daria
 */
public class ValidEnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;
    private boolean ignoreCase;
    private String[] availableValues;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.ignoreCase = constraintAnnotation.ignoreCase();
        this.availableValues = getEnumValues(enumClass);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = isValidEnumValue(value);

        if (!isValid && context != null) {
            context.disableDefaultConstraintViolation();
            String availableValuesStr = String.join(", ", availableValues);
            context.buildConstraintViolationWithTemplate(
                    "Значение '" + value + "' не существует для " + enumClass.getSimpleName() +
                            ". Доступные значения: " + availableValuesStr
            ).addConstraintViolation();
        }

        return isValid;
    }

    /**
     * Проверка на существование значения в классе enum
     */
    private boolean isValidEnumValue(String value) {
        try {
            if (ignoreCase) {
                Enum.valueOf((Class) enumClass, value.toUpperCase());
            } else {
                Enum.valueOf((Class) enumClass, value);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Получить список значение класса enum
     */
    private String[] getEnumValues(Class<? extends Enum<?>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }
}
