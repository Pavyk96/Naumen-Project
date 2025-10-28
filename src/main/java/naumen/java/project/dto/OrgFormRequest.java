package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO-запрос для создания или обновления организационно-правовой формы
 *
 * @author Daniil Mezev
 */
public record OrgFormRequest(
        @NotBlank @Size(min = 1, max = 10) String id,
        @NotBlank String name
) { }
