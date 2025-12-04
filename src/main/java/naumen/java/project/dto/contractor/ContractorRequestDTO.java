package naumen.java.project.dto.contractor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import naumen.java.project.validation.ValidUuid;

/**
 * DTO-запрос для создания или обновления контрагента
 *
 * @param id        Id контрагента
 * @param name      Имя контрагента
 * @param countryId Id страны контрагента
 * @param industryId Id индустрии контрагента
 * @param orgFormId Id организационно-правовой формы контрагента
 *
 * @author Daniil Mezev
 */
public record ContractorRequestDTO(
        @ValidUuid String id,
        @NotBlank String name,
        @NotBlank String countryId,
        @NotNull Long industryId,
        @NotNull String orgFormId
) { }

