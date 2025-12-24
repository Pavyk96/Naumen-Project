package naumen.java.project.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Валидирует, что строка является корректным UUID
 *
 * @author Daria
 */
@Documented
@Constraint(validatedBy = ValidUuidValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUuid {

    /** Сообщение об ошибке по умолчанию */
    String message() default "Невалидный UUID: {validatedValue}";

    /** Группы валидации */
    Class<?>[] groups() default {};

    /** Дополнительная информация */
    Class<? extends Payload>[] payload() default {};
}