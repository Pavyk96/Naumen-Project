package naumen.java.project.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Валидатор для проверки корректности UUID строк
 *
 * @author Daria
 */
@Component
public class UuidValidator implements ConstraintValidator<ValidUuid, String> {

    private String message;

    @Override
    public void initialize(ValidUuid constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            customizeMessage(context, "UUID cannot be empty");
            return false;
        }

        try {
            UUID.fromString(trimmedValue);
            return true;
        } catch (IllegalArgumentException e) {
            String customMessage = message.replace("{validatedValue}", trimmedValue);
            customizeMessage(context, customMessage);
            return false;
        }
    }

    /**
     * Настройка сообщения ошибки
     */
    private void customizeMessage(ConstraintValidatorContext context, String customMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(customMessage)
                .addConstraintViolation();
    }
}