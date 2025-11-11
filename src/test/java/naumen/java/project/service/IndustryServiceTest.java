package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.IndustryRequest;
import naumen.java.project.model.Industry;
import naumen.java.project.repository.IndustryRepository;
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
 * Тестирование IndustryService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
class IndustryServiceTest {

    @Mock
    private IndustryRepository repository;

    @InjectMocks
    private IndustryService service;

    private static final Long ID = 10L;
    private static final String NAME = "IT";
    private static final String NAME_UPDATED = "Information Technology";

    private Industry entity(Long id, String name) {
        return new Industry(id, name);
    }

    private IndustryRequest req(Long id, String name) {
        return new IndustryRequest(id, name);
    }

    /** Создать, затем получить все — вернуть 2 записи */
    @Test
    void create_then_list_industries_returnsTwo() {
        Industry existing = entity(1L, "Finance");
        Industry created  = entity(ID, NAME);

        Mockito.when(repository.existsById(ID)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(Industry.class))).thenReturn(created);
        Mockito.when(repository.findAll()).thenReturn(List.of(existing, created));

        Industry saved = service.create(req(ID, NAME));
        List<Industry> all = service.findAll();

        Assertions.assertEquals(ID, saved.getId());
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.stream().anyMatch(i -> ID.equals(i.getId())));

        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).save(ArgumentMatchers.any(Industry.class));
        Mockito.verify(repository).findAll();
    }

    /** Создать — id уже существует */
    @Test
    void create_industry_whenIdExists_throws() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req(ID, NAME))
        );

        Assertions.assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Вернуть все — одну запись */
    @Test
    void list_industries_returnsAll() {
        Mockito.when(repository.findAll()).thenReturn(List.of(entity(ID, NAME)));

        List<Industry> all = service.findAll();

        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals(ID, all.get(0).getId());
        Mockito.verify(repository).findAll();
    }

    /** Найти по id — найдено */
    @Test
    void get_industryById_found() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(entity(ID, NAME)));

        Industry result = service.findById(ID);

        Assertions.assertEquals(ID, result.getId());
        Assertions.assertEquals(NAME, result.getName());
        Mockito.verify(repository).findById(ID);
    }

    /** Найти по id — не найдено */
    @Test
    void get_industryById_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.findById(ID));

        Assertions.assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        Mockito.verify(repository).findById(ID);
    }

    /** Обновить — не найдено */
    @Test
    void update_industry_notFound() {
        Mockito.when(repository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.update(ID, req(ID, NAME_UPDATED)));

        Assertions.assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        Mockito.verify(repository).findById(ID);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить — изменить имя и сохранить ту же сущность */
    @Test
    void update_industry_updatesNameAndSavesSameInstance() {
        Industry existing = entity(ID, NAME);
        Mockito.when(repository.findById(ID)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(Industry.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Industry updated = service.update(ID, req(ID, NAME_UPDATED));

        Mockito.verify(repository).save(existing);
        Assertions.assertEquals(ID, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
        Assertions.assertSame(existing, updated);
    }

    /** Удалить — не существующую запись */
    @Test
    void delete_industry_notFound() {
        Mockito.when(repository.existsById(ID)).thenReturn(false);

        EntityNotFoundException ex =
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.delete(ID));

        Assertions.assertTrue(ex.getMessage().contains(String.valueOf(ID)));
        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.anyLong());
    }

    /** Удалить — существующую запись */
    @Test
    void delete_industry_ok() {
        Mockito.when(repository.existsById(ID)).thenReturn(true);

        service.delete(ID);

        Mockito.verify(repository).existsById(ID);
        Mockito.verify(repository).deleteById(ID);
    }

}
