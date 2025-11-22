package naumen.java.project.service;

import naumen.java.project.exepction.ResourceNotFoundException;
import naumen.java.project.model.Contractor;
import naumen.java.project.model.Country;
import naumen.java.project.model.Industry;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.ContractorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;


/**
 * Юнит-тесты для ContractorService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class ContractorServiceTest {

    private static final String ID = "c-1";
    private static final String NAME = "Acme LLC";
    private static final String UPDATED_NAME = "Acme Updated";

    private final ContractorRepository contractorRepositoryMock;
    private final ContractorService contractorService;

    private Country country;
    private Industry industry;
    private OrgForm orgForm;
    private Contractor contractor;
    private Contractor updatedContractor;

    public ContractorServiceTest(@Mock ContractorRepository contractorRepositoryMock) {
        this.contractorRepositoryMock = contractorRepositoryMock;
        this.contractorService = new ContractorService(contractorRepositoryMock);
    }

    /**
     * Инициализация исходных данных для тестов
     */
    @BeforeEach
    void setUpData() {
        country = new Country("RU", "Russia");
        industry = new Industry(10L, "IT");
        orgForm = new OrgForm("OOO", "ООО");

        contractor = new Contractor(ID, NAME, country, industry, orgForm);
        updatedContractor = new Contractor(ID, UPDATED_NAME, country, industry, orgForm);
    }

    /** Выброс ResourceNotFoundException, если нет ресурса с данным id */
    @Test
    void testResourceNotFoundException() {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> contractorService.findById(ID)
        );

        Assertions.assertEquals("Контрагент с id = " + ID + " не найден(а)", ex.getMessage());
    }

    /** Выброс IllegalArgumentException, если ресурс уже существует */
    @Test
    void testIllegalArgumentExceptionByExistsById() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contractorService.create(contractor)
        );
        Assertions.assertEquals("Контрагент с id = " + ID + " уже существует", ex.getMessage());
    }


    /** Проверяет, что update обновляет существующего контрагента, если id совпадают */
    @Test
    void testUpdateUpdatesExistingContractor() throws ResourceNotFoundException {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.of(contractor));
        Mockito.when(contractorRepositoryMock.save(Mockito.any(Contractor.class)))
                .thenAnswer(inv -> inv.getArgument(0, Contractor.class));
        Contractor result = contractorService.update(ID, updatedContractor);

        Assertions.assertSame(contractor, result);
        Assertions.assertEquals(UPDATED_NAME, result.getName());
        Assertions.assertSame(country, result.getCountry());
        Assertions.assertSame(industry, result.getIndustry());
        Assertions.assertSame(orgForm, result.getOrgForm());
    }

    /** Выброс IllegalArgumentException, если id ресура не совпадает с аргументом */
    @Test
    void testIllegalArgumentException() {
        Contractor body = new Contractor("another-id", NAME, country, industry, orgForm);
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> contractorService.update(ID, body)
        );

        Assertions.assertEquals(
                "Идентификатор в пути (" + ID +
                        ") не совпадает с идентификатором в теле запроса (" + "another-id" + ")",
                ex.getMessage()
        );
    }

    /** Кидает ResourceNotFoundException, при existsById */
    @Test
    void testResourceNotFoundExceptionByDelete() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(false);

        ResourceNotFoundException ex = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> contractorService.delete(ID)
        );

        Assertions.assertEquals("Контрагент с id = " + ID + " не найден(а)", ex.getMessage());
    }
}
