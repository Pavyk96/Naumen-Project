package naumen.java.project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Валидирует, что строка содержит существующие значение enum
 *
 * @author Daria
 */
@Documented
@Constraint(validatedBy = ValidEnumValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
    /** Сообщение об ошибке по умолчанию */
    String message() default "Недопустимое значение enum";
    /** Группы валидации */
    Class<?>[] groups() default {};
    /** Дополнительная информация */
    Class<? extends Payload>[] payload() default {};
    /** Класс enum */
    Class<? extends Enum<?>> enumClass();
    /** Игнорирование регистра */
    boolean ignoreCase() default true;
}