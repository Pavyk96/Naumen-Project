package naumen.java.project.dto.contractor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO-запрос для создания или обновления контрагента
 *
 * @author Daniil Mezev
 */
public record ContractorRequestDTO(
        /**
         * Id контрагента
         */
        @NotBlank String id,
        /**
         * Имя контрагента
         */
        @NotBlank String name,
        /**
         * Id страны контрагента
         */
        @NotBlank String countryId,
        /**
         * Id индустрии контрагента
         */
        @NotNull Long industryId,
        /**
         * Id организационно-правовой формы контрагента
         */
        @NotNull String orgFormId
) { }
