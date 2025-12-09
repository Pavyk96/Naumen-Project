package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO-запрос для создания или обновления организационно-правовой формы
 *
 * @param id   Id правовой формы
 * @param name Название правовой формы
 *
 * @author Daniil Mezev
 */
public record OrgFormRequestDTO(
        @NotBlank String id,
        @NotBlank String name
) { }
