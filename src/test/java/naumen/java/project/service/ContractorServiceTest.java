package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.contractor.ContractorRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Тестирование ContractorService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class ContractorServiceTest {

    @Mock
    private ContractorRepository repository;

    @InjectMocks
    private ContractorService service;

    private static final String ID = "c-1";
    private static final String NAME = "Acme LLC";
    private static final String NAME_UPDATED = "Acme Updated";
    private static final String COUNTRY_ID = "RU";
    private static final Long   INDUSTRY_ID = 10L;
    private static final String ORGFORM_ID = "OOO";

    private static final String COUNTRY_ID_UPDATED = "US";
    private static final Long   INDUSTRY_ID_UPDATED = 20L;
    private static final String ORGFORM_ID_UPDATED = "AO";

    private Contractor entity(String id, String name, String countryId, Long industryId, String orgFormId) {
        return new Contractor(id, name, countryId, industryId, orgFormId);
    }

    private Contractor entity(String name) {
        return entity(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    /** DTO-запрос */
    private ContractorRequest req(String id, String name, String countryId, Long industryId, String orgFormId) {
        return new ContractorRequest(id, name, countryId, industryId, orgFormId);
    }

    /** Создать, затем получить все — вернуть 2 записи */
    @Test
    void create_then_list_contractors_returnsTwo() {
        Contractor existing = entity("c-0", "First", COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
        Contractor toCreate = entity(ID, NAME, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);

        Mockito.when(repository.existsById(ID)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(Contractor.class))).thenReturn(toCreate);
        Mockito.when(repository.findAll()).thenReturn(List.of(existing, toCreate));

        Contractor created = service.create(req(ID, NAME, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID));
        List<Contractor> all = service.findAll();

        Assertions.assertEquals(NAME, created.getName());
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.stream().anyMatch(c -> ID.equals(c.getId())));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).save(ArgumentMatchers.any(Contractor.class));
        Mockito.verify(repository).findAll();
    }

    /** Создать — id уже существует */
    @Test
    void create_contractor_whenIdExists_throws() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req(ID, NAME, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Найти по id — найдено */
    @Test
    void get_contractorById_found() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(entity(NAME)));

        Contractor c = service.findById(ID);

        Assertions.assertEquals(ID, c.getId());
        Assertions.assertEquals(NAME, c.getName());
        Mockito.verify(repository).findById(ID);
    }

    /** Найти по id — не найдено */
    @Test
    void get_contractorById_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.findById(ID));

        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).findById(ID);
    }

    /** Обновить — не найдено */
    @Test
    void update_contractor_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(ID, req(ID, NAME_UPDATED, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).findById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить — изменить поля и сохранить ту же сущность */
    @Test
    void update_contractor_updatesFieldsAndSavesSameInstance() {
        Contractor existing = entity(ID, NAME, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(Contractor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ContractorRequest updateReq = req(
                ID, NAME_UPDATED, COUNTRY_ID_UPDATED, INDUSTRY_ID_UPDATED, ORGFORM_ID_UPDATED
        );

        Contractor result = service.update(ID, updateReq);

        ArgumentCaptor<Contractor> captor = ArgumentCaptor.forClass(Contractor.class);
        Mockito.verify(repository).save(captor.capture());
        Contractor saved = captor.getValue();
        Assertions.assertSame(existing, saved);

        Assertions.assertEquals(ID, saved.getId());

        Assertions.assertEquals(NAME_UPDATED, saved.getName());
        Assertions.assertEquals(COUNTRY_ID_UPDATED, saved.getCountryId());
        Assertions.assertEquals(INDUSTRY_ID_UPDATED, saved.getIndustryId());
        Assertions.assertEquals(ORGFORM_ID_UPDATED, saved.getOrgFormId());

        Assertions.assertSame(saved, result);
    }

    /** Удалить — существующую запись */
    @Test
    void delete_contractor_ok() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);
        // Сервис внутри delete() запрашивает контрагента со сделками
        Mockito.when(repository.findWithDealsById(ID))
                .thenReturn(Optional.of(entity(NAME)));

        service.delete(ID);

        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).findWithDealsById(ID);
        Mockito.verify(repository).deleteById(ID);
    }

    /** Удалить — запись не найдена при findWithDealsById */
    @Test
    void delete_contractor_notFoundOnWithDeals() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);
        Mockito.when(repository.findWithDealsById(ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.delete(ID));

        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).findWithDealsById(ID);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.any());
    }

}
