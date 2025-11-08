package naumen.java.project.service;

import jakarta.persistence.EntityNotFoundException;
import naumen.java.project.dto.CountryRequest;
import naumen.java.project.model.Country;
import naumen.java.project.repository.CountryRepository;
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
 * Тестирование CountryService
 *
 * @author Daniil Mezev
 */
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

    /** Создание и возврат записей */
    @Test
    void create_then_list_countries_returnsTwo() {
        Country existing = entity("US", "United States");
        Country created = entity(ID_NORM, NAME);

        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);
        Mockito.when(repository.save(ArgumentMatchers.any(Country.class)))
                .thenReturn(created);
        Mockito.when(repository.findAll())
                .thenReturn(List.of(existing, created));

        Country saved = service.create(req(ID_RAW, NAME));
        List<Country> all = service.findAll();

        Assertions.assertEquals(ID_NORM, saved.getId());
        Assertions.assertEquals(2, all.size());
        Assertions.assertTrue(all.stream().anyMatch(c -> ID_NORM.equals(c.getId())));

        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository).save(ArgumentMatchers.any(Country.class));
        Mockito.verify(repository).findAll();
    }

    /** Создать запись — id уже существует */
    @Test
    void create_country_whenIdExists_throws() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(req(ID_RAW, NAME))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Найти запись по id — с нормализацией найдено */
    @Test
    void get_countryById_found_withNormalization() {
        Mockito.when(repository.findById(ID_NORM))
                .thenReturn(Optional.of(entity(ID_NORM, NAME)));

        Country c = service.findById(ID_RAW);

        Assertions.assertEquals(ID_NORM, c.getId());
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Найти запись по id — не найдено */
    @Test
    void get_countryById_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.findById(ID_RAW)
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
    }

    /** Обновить запись — не найдено */
    @Test
    void update_country_notFound() {
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.empty());

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.update(ID_RAW, req(ID_RAW, NAME_UPDATED))
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).findById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).save(ArgumentMatchers.any());
    }

    /** Обновить запись — изменить имя и сохранить ту же сущность */
    @Test
    void update_country_updatesNameAndSavesSameInstance() {
        Country existing = entity(ID_NORM, NAME);
        Mockito.when(repository.findById(ID_NORM)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(ArgumentMatchers.any(Country.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Country result = service.update(ID_RAW, req(ID_RAW, NAME_UPDATED));

        // сохраняется та же ссылка (мутация), а не новый объект
        Mockito.verify(repository).save(existing);
        Assertions.assertEquals(ID_NORM, existing.getId());
        Assertions.assertEquals(NAME_UPDATED, existing.getName());
        Assertions.assertSame(existing, result);
    }

    /** Удалить запись — не существующую запись */
    @Test
    void delete_country_notFound() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(false);

        EntityNotFoundException ex = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> service.delete(ID_RAW)
        );

        Assertions.assertTrue(ex.getMessage().contains(ID_NORM));
        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository, Mockito.never()).deleteById(ArgumentMatchers.anyString());
    }

    /** Удалить запись — существующую запись */
    @Test
    void delete_country_ok() {
        Mockito.when(repository.existsById(ID_NORM)).thenReturn(true);

        service.delete(ID_RAW);

        Mockito.verify(repository).existsById(ID_NORM);
        Mockito.verify(repository).deleteById(ID_NORM);
    }

}
