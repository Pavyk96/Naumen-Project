package naumen.java.project.dto.contractor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO-запрос для создания или обновления контрагента
 *
 * @author Daniil Mezev
 */
public record ContractorRequest(
        @NotBlank String id,
        @NotBlank String name,
        @NotBlank String countryId,
        @NotNull Long industryId,
        @NotNull String orgFormId
) { }
