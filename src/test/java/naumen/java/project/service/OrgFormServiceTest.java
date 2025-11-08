package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.OrgFormRequest;
import naumen.java.project.model.OrgForm;
import naumen.java.project.repository.OrgFormRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

/**
 * Тестирование OrgFormService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class OrgFormServiceTest {

    @Mock
    private OrgFormRepository repository;

    @InjectMocks
    private OrgFormService service;

    private static final String ID_RAW = " ooo ";
    private static final String ID_NORM = "OOO";
    private static final String NAME = "Общество с ограниченной ответственностью";
    private static final String NAME_UPDATED = "ООО (обновлено)";

    private OrgForm entity(String id, String name) { return new OrgForm(id, name); }
    private OrgFormRequest req(String id, String name) { return new OrgFormRequest(id, name); }

    /** Создать, затем получить все — вернуть 2 записи */
    @Test
    void create_then_list_orgForms_returnsTwo() {
        OrgForm existing = entity("PJSC", "Публичное акционерное общество");
        OrgForm created  = entity(ID_NORM, NAME);

        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(OrgForm.class))).thenReturn(created);
        Mockito.when(repository.findAll()).thenReturn(List.of(existing, created));

        OrgForm saved = service.create(req(ID_RAW, NAME));
        List<OrgForm> all = service.findAll();

        Assertions.assertEquals(ID_NORM, saved.getId());
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.stream().anyMatch(of -> ID_NORM.equals(of.getId())));

        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository).save(ArgumentMatchers.any(OrgForm.class));
        Mockito.verify(repository).findAll();
    }

    /** Создать — id существует */
    @Test
    void create_orgForm_whenIdExists_throws() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req(ID_RAW, NAME))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Вернуть все — одну запись */
    @Test
    void list_orgForms_returnsAll() {
        Mockito.when(repository.findAll()).thenReturn(List.of(entity(ID_NORM, NAME)));

        List<OrgForm> all = service.findAll();

        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals(ID_NORM, all.get(0).getId());
        Mockito.verify(repository).findAll();
    }

    /** Найти по id — с нормализацией найдено */
    @Test
    void get_orgFormById_found_withNormalization() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.of(entity(ID_NORM, NAME)));

        OrgForm of = service.findById(ID_RAW);

        Assertions.assertEquals(ID_NORM, of.getId());
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Найти по id — не найдено */
    @Test
    void get_orgFormById_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.findById(ID_RAW));

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Обновить — не найдено */
    @Test
    void update_orgForm_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(ID_RAW, req(ID_RAW, NAME_UPDATED))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить — изменить name и сохранить ту же сущность */
    @Test
    void update_orgForm_updatesNameAndSavesSameInstance() {
        OrgForm existing = entity(ID_NORM, NAME);
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(OrgForm.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        OrgForm updated = service.update(ID_RAW, req(ID_RAW, NAME_UPDATED));

        Mockito.verify(repository).save(existing);
        Assertions.assertEquals(ID_NORM, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
        Assertions.assertSame(existing, updated);
    }

    /** Удалить — не существующую запись */
    @Test
    void delete_orgForm_notFound() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.delete(ID_RAW));

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.anyString());
    }

    /** Удалить — существующую запись */
    @Test
    void delete_orgForm_ok() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository).deleteById(ID_NORM);
    }

}
