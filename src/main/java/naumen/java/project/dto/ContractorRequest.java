package naumen.java.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO-запрос для создания или обновления контрагента
 *
 * @author Daniil Mezev
 */
public record ContractorRequest(
        @NotBlank @Size(min = 1, max = 36) String id,
        @NotBlank @Size(min = 1, max = 256) String name,
        @NotBlank @Size(min = 2, max = 3) String countryId,
        @NotNull @Size(min = 1, max = 10) Long industryId,
        @NotNull @Size(min = 1, max = 10) String orgFormId
) { }
