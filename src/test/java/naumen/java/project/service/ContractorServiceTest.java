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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    private ContractorRepository contractorRepositoryMock;
    private ContractorService contractorService;

    private Country country;
    private Industry industry;
    private OrgForm orgForm;
    private Contractor contractor;
    private Contractor updatedContractor;

    public ContractorServiceTest(@Mock ContractorRepository contractorRepositoryMock) {
        this.contractorRepositoryMock = contractorRepositoryMock;
        this.contractorService = new ContractorService(contractorRepositoryMock);
    }

    @BeforeEach
    void setUpData() {
        country = new Country("RU", "Russia");
        industry = new Industry(10L, "IT");
        orgForm = new OrgForm("OOO", "ООО");

        contractor = new Contractor(ID, NAME, country, industry, orgForm);
        updatedContractor = new Contractor(ID, UPDATED_NAME, country, industry, orgForm);
    }

    /** Проверяет, что findAll возвращает список контрагентов из репозитория */
    @Test
    void testFindAllReturnsAllContractors() {
        Mockito.when(contractorRepositoryMock.findAll()).thenReturn(List.of(contractor));

        List<Contractor> result = contractorService.findAll();

        assertEquals(1, result.size());
        assertSame(contractor, result.get(0));
    }

    /** Проверяет, что findById возвращает контрагента при его наличии */
    @Test
    void testFindByIdReturnsContractor() throws ResourceNotFoundException {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.of(contractor));

        Contractor result = contractorService.findById(ID);

        assertSame(contractor, result);
    }

    /** Проверяет, что findById выбрасывает ResourceNotFoundException, если контрагент не найден */
    @Test
    void testFindByIdThrowsIfContractorNotFound() {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractorService.findById(ID));
    }

    /** Проверяет, что findByIdWithDeals возвращает контрагента при его наличии */
    @Test
    void testFindByIdWithDealsReturnsContractor() throws ResourceNotFoundException {
        Mockito.when(contractorRepositoryMock.findWithDealsById(ID)).thenReturn(Optional.of(contractor));

        Contractor result = contractorService.findByIdWithDeals(ID);

        assertSame(contractor, result);
    }

    /** Проверяет, что findByIdWithDeals выбрасывает ResourceNotFoundException, если контрагент не найден */
    @Test
    void testFindByIdWithDealsThrowsIfContractorNotFound() {
        Mockito.when(contractorRepositoryMock.findWithDealsById(ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contractorService.findByIdWithDeals(ID));
    }

    /** Проверяет, что create сохраняет нового контрагента, если id ещё не существует */
    @Test
    void testCreateSavesNewContractor() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(false);
        Mockito.when(contractorRepositoryMock.save(contractor)).thenReturn(contractor);

        Contractor result = contractorService.create(contractor);

        assertSame(contractor, result);
    }

    /** Проверяет, что create выбрасывает IllegalArgumentException, если id уже существует */
    @Test
    void testCreateThrowsIfContractorAlreadyExists() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contractorService.create(contractor)
        );
        assertTrue(ex.getMessage().contains(ID));
    }

    /** Проверяет, что update обновляет существующего контрагента, если id совпадают */
    @Test
    void testUpdateUpdatesExistingContractor() throws ResourceNotFoundException {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.of(contractor));
        Mockito.when(contractorRepositoryMock.save(Mockito.any(Contractor.class)))
                .thenAnswer(inv -> inv.getArgument(0, Contractor.class));
        Contractor result = contractorService.update(ID, updatedContractor);

        assertSame(contractor, result);
        assertEquals(UPDATED_NAME, result.getName());
        assertSame(country, result.getCountry());
        assertSame(industry, result.getIndustry());
        assertSame(orgForm, result.getOrgForm());
    }

    /** Проверяет, что update выбрасывает IllegalArgumentException, если id в пути и теле не совпадают */
    @Test
    void testUpdateThrowsIfIdsDoNotMatch() {
        Contractor body = new Contractor("another-id", NAME, country, industry, orgForm);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contractorService.update(ID, body)
        );

        assertTrue(ex.getMessage().contains(ID));
        assertTrue(ex.getMessage().contains("another-id"));
        Mockito.verify(contractorRepositoryMock, Mockito.never()).findById(Mockito.anyString());
    }

    /** Проверяет, что update выбрасывает ResourceNotFoundException, если контрагент не найден */
    @Test
    void testUpdateThrowsIfContractorNotFound() {
        Mockito.when(contractorRepositoryMock.findById(ID)).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                () -> contractorService.update(ID, updatedContractor)
        );
    }

    /** Проверяет, что delete удаляет контрагента, если он существует */
    @Test
    void testDeleteDeletesContractorIfExists() throws ResourceNotFoundException {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(true);
        contractorService.delete(ID);
        Mockito.verify(contractorRepositoryMock).deleteById(ID);
    }

    /** Проверяет, что delete выбрасывает ResourceNotFoundException, если контрагент не найден */
    @Test
    void testDeleteThrowsIfContractorNotFound() {
        Mockito.when(contractorRepositoryMock.existsById(ID)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> contractorService.delete(ID));
        Mockito.verify(contractorRepositoryMock, Mockito.never()).deleteById(Mockito.anyString());
    }
}
