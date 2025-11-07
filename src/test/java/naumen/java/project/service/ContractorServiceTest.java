package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.contractor.ContractorRequest;
import naumen.java.project.model.Contractor;
import naumen.java.project.repository.ContractorRepository;
import org.junit.jupiter.api.DisplayName;
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
    private static final Long INDUSTRY_ID = 10L;
    private static final String ORGFORM_ID = "OOO";

    private Contractor entity(String name) {
        return new Contractor(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    /** Создание реквеста */
    private ContractorRequest req(String name) {
        return new ContractorRequest(ID, name, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);
    }

    /** Вернуть все записи */
    @Test
    @DisplayName("findAll: вернуть все записи")
    void findAll_ok() {
        Mockito.when(repository.findAll()).thenReturn(List.of(entity(NAME)));

        List<Contractor> result = service.findAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(NAME, result.get(0).getName());
        Mockito.verify(repository).findAll();
    }

    /** Вернуть запись по id (если найдено) */
    @Test
    @DisplayName("findById: найден")
    void findById_found() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(entity(NAME)));

        Contractor c = service.findById(ID);

        Assertions.assertEquals(ID, c.getId());
        Assertions.assertEquals(NAME, c.getName());
        Mockito.verify(repository).findById(ID);
    }

    /** Вернуть запись по id — иначе ошибка EntityNotFoundException */
    @Test
    @DisplayName("findById: не найден -> EntityNotFoundException")
    void findById_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.findById(ID));
        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).findById(ID);
    }

    /** Создать запись — если id уже существует */
    @Test
    @DisplayName("create: если id уже существует -> IllegalArgumentException")
    void create_alreadyExists() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex =
                Assertions.assertThrows(IllegalArgumentException.class, () -> service.create(req(NAME)));
        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Создать запись */
    @Test
    @DisplayName("create: сохраняет сущность, собранную из DTO")
    void create_ok() {
        Mockito.when(repository.existsById(ID)).thenReturn(false);
        ArgumentCaptor<Contractor> captor = ArgumentCaptor.forClass(Contractor.class);
        Mockito.when(repository.save(ArgumentMatchers.any(Contractor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Contractor created = service.create(req(NAME));

        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).save(captor.capture());
        Contractor toSave = captor.getValue();
        Assertions.assertEquals(ID, toSave.getId());
        Assertions.assertEquals(NAME, toSave.getName());
        Assertions.assertEquals(COUNTRY_ID, toSave.getCountryId());
        Assertions.assertEquals(INDUSTRY_ID, toSave.getIndustryId());
        Assertions.assertEquals(ORGFORM_ID, toSave.getOrgFormId());
        Assertions.assertEquals(NAME, created.getName());
    }

    /** Обновить запись — если id не совпадает */
    @Test
    @DisplayName("update: если path id != body id -> IllegalArgumentException")
    void update_idMismatch() {
        ContractorRequest otherReq =
                new ContractorRequest("other", NAME_UPDATED, COUNTRY_ID, INDUSTRY_ID, ORGFORM_ID);

        IllegalArgumentException ex =
                Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(ID, otherReq));
        Assertions.assertTrue(ex.getMessage().contains("Path id"));
        Mockito.verify(repository, Mockito.never()).findById(ArgumentMatchers.any());
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить запись — если id не существует */
    @Test
    @DisplayName("update: когда не найден -> EntityNotFoundException")
    void update_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.update(ID, req(NAME_UPDATED)));
        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).findById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить запись */
    @Test
    @DisplayName("update: обновляет изменяемые поля и сохраняет")
    void update_ok() {
        Contractor existing = entity(NAME);
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(Contractor.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Contractor updated = service.update(ID, req(NAME_UPDATED));

        Assertions.assertEquals(NAME_UPDATED, updated.getName());
        Assertions.assertEquals(COUNTRY_ID, updated.getCountryId());
        Assertions.assertEquals(INDUSTRY_ID, updated.getIndustryId());
        Assertions.assertEquals(ORGFORM_ID, updated.getOrgFormId());
        Mockito.verify(repository).findById(ID);
        Mockito.verify(repository).save(existing);
    }

    /** Удалить запись — если id не существует */
    @Test
    @DisplayName("delete: когда не существует -> EntityNotFoundException")
    void delete_notFound() {
        Mockito.when(repository.existsById(ID)).thenReturn(false);

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.delete(ID));
        Assertions.assertTrue(ex.getMessage().contains(ID));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.any());
    }

    /** Удалить запись */
    @Test
    @DisplayName("delete: удаляет по id")
    void delete_ok() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);
        Mockito.when(repository.findWithDealsById(ID)).thenReturn(Optional.of(entity(NAME)));

        service.delete(ID);

        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).deleteById(ID);
    }

}
