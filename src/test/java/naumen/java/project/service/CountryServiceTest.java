package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
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
class CountryServiceTest {

    @Mock
    private CountryRepository repository;

    @InjectMocks
    private CountryService service;

    private static final String ID_RAW = " ru ";
    private static final String ID_NORM = "RU";
    private static final String NAME = "Russia";
    private static final String NAME_UPDATED = "Russian Federation";

    private Country entity(String id, String name) {
        return new Country(id, name);
    }

    private CountryRequest req(String id, String name) {
        return new CountryRequest(id, name);
    }

    /** Вернуть все записи */
    @Test
    @DisplayName("findAll: вернуть все записи")
    void findAll_ok() {
        Mockito.when(repository.findAll()).thenReturn(List.of(entity(ID_NORM, NAME)));

        List<Country> all = service.findAll();

        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals(ID_NORM, all.get(0).getId());
        Mockito.verify(repository).findAll();
    }

    /** Вернуть запись по id (если найдено) */
    @Test
    @DisplayName("findById: нормализует id и возвращает страну")
    void findById_ok_withNormalization() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.of(entity(ID_NORM, NAME)));

        Country c = service.findById(ID_RAW);

        Assertions.assertEquals(ID_NORM, c.getId());
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Вернуть запись по id — иначе ошибка EntityNotFoundException */
    @Test
    @DisplayName("findById: не найден -> EntityNotFoundException")
    void findById_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.findById(ID_RAW));
        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Создать запись — если id уже существует */
    @Test
    @DisplayName("create: конфликт id -> IllegalArgumentException")
    void create_conflict() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(req(ID_RAW, NAME)));
        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Создать запись */
    @Test
    @DisplayName("create: нормализует id и сохраняет")
    void create_ok() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(Country.class))).thenAnswer(inv -> inv.getArgument(0));

        Country created = service.create(req(ID_RAW, NAME));

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        Mockito.verify(repository).save(captor.capture());
        Country toSave = captor.getValue();

        Assertions.assertEquals(ID_NORM, toSave.getId());
        Assertions.assertEquals(NAME, toSave.getName());
        Assertions.assertEquals(ID_NORM, created.getId());
    }

    /** Обновить запись — если id не совпадает */
    @Test
    @DisplayName("update: несовпадение path/body после нормализации -> IllegalArgumentException")
    void update_idMismatch() {
        CountryRequest body = req("us", NAME_UPDATED);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.update(ID_RAW, body));
        Assertions.assertTrue(ex.getMessage().contains("Path id and body id must be equal"));
        Mockito.verify(repository, Mockito.never()).findById(ArgumentMatchers.anyString());
    }

    /** Обновить запись — если id не существует */
    @Test
    @DisplayName("update: не найден -> EntityNotFoundException")
    void update_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.update(ID_RAW, req(ID_RAW, NAME_UPDATED)));
        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить запись */
    @Test
    @DisplayName("update: обновляет имя и сохраняет")
    void update_ok() {
        Country existing = entity(ID_NORM, NAME);
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(Country.class))).thenAnswer(inv -> inv.getArgument(0));

        Country updated = service.update(ID_RAW, req(ID_RAW, NAME_UPDATED));

        Assertions.assertEquals(ID_NORM, updated.getId());
        Assertions.assertEquals(NAME_UPDATED, updated.getName());
        Mockito.verify(repository).findById(ID_NORM);
        Mockito.verify(repository).save(existing);
    }

    /** Удалить запись — если id не существует */
    @Test
    @DisplayName("delete: не найден -> EntityNotFoundException")
    void delete_notFound() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.delete(ID_RAW));
        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.anyString());
    }

    /** Удалить запись */
    @Test
    @DisplayName("delete: нормализует id и удаляет")
    void delete_ok() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository).deleteById(ID_NORM);
    }

}
