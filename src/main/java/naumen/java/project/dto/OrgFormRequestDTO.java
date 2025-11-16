package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO-запрос для создания или обновления организационно-правовой формы
 *
 * @author Daniil Mezev
 */
public record OrgFormRequestDTO(
        /**
         * Id страны
         */
        @NotBlank String id,
        /**
         * Название правовой формы
         */
        @NotBlank String name
) { }
